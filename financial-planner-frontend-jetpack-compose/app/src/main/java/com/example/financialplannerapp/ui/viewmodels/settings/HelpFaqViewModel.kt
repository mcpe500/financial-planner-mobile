// <!-- filepath: app/src/main/java/com/example/financialplannerapp/ui/viewmodels/settings/HelpFaqViewModel.kt -->
package com.example.financialplannerapp.ui.viewmodels.settings

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.HelpContentRepository
import com.example.financialplannerapp.db.HelpContentDao
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HelpFaqUiState(
    val faqs: List<FAQItem> = emptyList(),
    val helpContents: List<HelpContent> = emptyList(),
    val filteredFaqs: List<FAQItem> = emptyList(),
    val filteredHelpContents: List<HelpContent> = emptyList(),
    val isLoading: Boolean = false,
    val isOnline: Boolean = false,
    val lastUpdateText: String = "Terakhir diperbarui: Belum pernah",
    val syncStatusText: String = "Konten tersedia offline",
    val syncStatusColor: Int = Color.parseColor("#FFA500"), // Orange
    val searchQuery: String = "",
    val selectedTab: Int = 0 // 0 for FAQ, 1 for Guide, 2 for Contact
)

class HelpFaqViewModel(
    application: Application,
    private val helpContentRepository: HelpContentRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HelpFaqUiState())
    val uiState: StateFlow<HelpFaqUiState> = _uiState.asStateFlow()

    private val TAG = "HelpFaqViewModel"

    init {
        loadOfflineContent()
        updateSyncStatus()
    }

    private fun loadOfflineContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val existingFAQs = helpContentRepository.getFaqsByCategory("general")
                val existingGuides = helpContentRepository.getHelpContentByCategory("guide")

                if (existingFAQs.isEmpty()) {
                    helpContentRepository.insertAllFAQs(getDefaultFAQs())
                }
                if (existingGuides.isEmpty()) {
                    helpContentRepository.insertAllHelpContent(getDefaultGuides())
                }

                val faqs = helpContentRepository.getFaqsByCategory("general")
                val guides = helpContentRepository.getHelpContentByCategory("guide")

                _uiState.value = _uiState.value.copy(
                    faqs = faqs,
                    helpContents = guides,
                    filteredFaqs = if (_uiState.value.searchQuery.isBlank()) faqs else helpContentRepository.searchFAQs(_uiState.value.searchQuery),
                    filteredHelpContents = if (_uiState.value.searchQuery.isBlank()) guides else helpContentRepository.searchHelpContent(_uiState.value.searchQuery),
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading offline content: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterContent(query)
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }
    
    private fun filterContent(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    filteredFaqs = _uiState.value.faqs,
                    filteredHelpContents = _uiState.value.helpContents
                )
            } else {
                try {
                    val filteredFaqs = helpContentRepository.searchFAQs(query)
                    val filteredHelpContents = helpContentRepository.searchHelpContent(query)
                    _uiState.value = _uiState.value.copy(
                        filteredFaqs = filteredFaqs,
                        filteredHelpContents = filteredHelpContents
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error filtering content: ${e.message}", e)
                }
            }
        }
    }

    fun updateSyncStatus() {
        viewModelScope.launch {
            val isOnline = NetworkUtils.isNetworkAvailable(getApplication())
            val lastFaqUpdate = helpContentRepository.getFaqLastUpdateTime()
            // You might want a more specific last update time for guides if they are separate
            val lastGuideUpdate = helpContentRepository.getHelpContentLastUpdateTime("guide")


            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val lastUpdateTimestamp = maxOf(lastFaqUpdate ?: 0L, lastGuideUpdate ?: 0L)

            val lastUpdateText = if (lastUpdateTimestamp > 0) {
                "Terakhir diperbarui: ${dateFormat.format(Date(lastUpdateTimestamp))}"
            } else {
                "Terakhir diperbarui: Belum pernah"
            }

            val syncStatusText = if (isOnline) "Konten dapat diperbarui" else "Konten tersedia offline"
            val syncStatusColor = if (isOnline) Color.GREEN else Color.parseColor("#FFA500")

            _uiState.value = _uiState.value.copy(
                isOnline = isOnline,
                lastUpdateText = lastUpdateText,
                syncStatusText = syncStatusText,
                syncStatusColor = syncStatusColor
            )
        }
    }

    fun updateContentFromServer() {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            // Show toast or error message via state
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                delay(2000) // Simulate API call

                // In a real app, fetch from API and update Room
                // For now, just refresh local data and update timestamps (implicitly done by Room entities)
                loadOfflineContent() // This will re-fetch from DAO, which has updated timestamps
                updateSyncStatus() // This will pick up the new timestamps

            } catch (e: Exception) {
                Log.e(TAG, "Error updating content: ${e.message}", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun getDefaultFAQs(): List<FAQItem> {
         return listOf(
            FAQItem(id = "faq_1", question = "Bagaimana cara menambahkan transaksi baru?", answer = "Untuk menambahkan transaksi baru:\n1. Buka halaman Transaksi...", category = "general", order = 1, isPopular = true),
            FAQItem(id = "faq_2", question = "Bagaimana cara melihat laporan keuangan?", answer = "Laporan keuangan dapat dilihat di:\n1. Menu Laporan...", category = "general", order = 2, isPopular = true),
            FAQItem(id = "faq_3", question = "Apakah data saya aman?", answer = "Ya, data Anda aman karena:\n• Disimpan secara lokal...", category = "general", order = 3, isPopular = false),
            // Add more default FAQs
        )
    }

    private fun getDefaultGuides(): List<HelpContent> {
        return listOf(
            HelpContent(id = "guide_1", title = "Panduan Memulai", content = "Selamat datang di Financial Planner!...", category = "guide", order = 1),
            HelpContent(id = "guide_2", title = "Mengelola Anggaran", content = "Cara membuat anggaran yang efektif:\n\n1. Tetapkan limit...", category = "guide", order = 2),
            // Add more default guides
        )
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HelpFaqViewModel::class.java)) {
                val db = AppDatabase.getDatabase(application)
                val repository = HelpContentRepository(db.helpContentDao())
                return HelpFaqViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}