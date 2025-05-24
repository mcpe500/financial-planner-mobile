package com.example.financialplannerapp.ui.viewmodels.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.repository.HelpContentRepository
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.utils.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HelpAndFaqUiState(
    val faqs: List<FAQItem> = emptyList(),
    val helpArticles: List<HelpContent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class HelpAndFaqViewModel(application: Application) : AndroidViewModel(application) {

    private val helpContentRepository: HelpContentRepository
    private val _uiState = MutableStateFlow(HelpAndFaqUiState())
    val uiState: StateFlow<HelpAndFaqUiState> = _uiState.asStateFlow()

    init {
        val helpContentDao = AppDatabase.getDatabase(application).helpContentDao()
        helpContentRepository = HelpContentRepository(helpContentDao)
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load some default categories or all if not too many
                val generalFaqs = helpContentRepository.getFaqsByCategory("general")
                val popularFaqs = helpContentRepository.getFaqsByCategory("popular") // Assuming a "popular" category
                val allFaqs = (generalFaqs + popularFaqs).distinctBy { it.id }


                val guides = helpContentRepository.getHelpContentByCategory("guides")
                // Add more categories as needed

                _uiState.value = _uiState.value.copy(
                    faqs = allFaqs.sortedBy { it.order },
                    helpArticles = guides.sortedBy { it.order },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load help content: ${e.message}")
            }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadInitialData() // Or load from a cached "all items" list
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val searchedFaqs = helpContentRepository.searchFAQs(query)
                val searchedHelp = helpContentRepository.searchHelpContent(query)
                _uiState.value = _uiState.value.copy(
                    faqs = searchedFaqs.sortedBy { it.order },
                    helpArticles = searchedHelp.sortedBy { it.order },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Search failed: ${e.message}")
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        loadInitialData()
    }

    // Example: Refresh data from a remote source (not implemented yet)
    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Placeholder for fetching new data from API and updating Room
            // val newFaqs = apiService.fetchFaqs()
            // helpContentRepository.clearAllFAQs()
            // helpContentRepository.insertAllFAQs(newFaqs)
            // val newHelp = apiService.fetchHelpContent()
            // helpContentRepository.clearAllHelpContent()
            // helpContentRepository.insertAllHelpContent(newHelp)
            loadInitialData() // Reload from Room
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}