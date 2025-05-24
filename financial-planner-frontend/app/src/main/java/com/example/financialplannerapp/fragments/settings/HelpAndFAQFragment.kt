package com.example.financialplannerapp.fragments.settings

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financialplannerapp.R
import com.example.financialplannerapp.adapters.FAQAdapter
import com.example.financialplannerapp.adapters.HelpContentAdapter
import com.example.financialplannerapp.databinding.FragmentHelpAndFAQBinding
import com.example.financialplannerapp.db.HelpContentDao
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.NetworkUtils
import com.example.financialplannerapp.utils.TokenManager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HelpAndFAQFragment : Fragment() {
    private var _binding: FragmentHelpAndFAQBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var helpContentDao: HelpContentDao
    private lateinit var tokenManager: TokenManager
    
    private var currentFAQs = listOf<FAQItem>()
    private var currentHelpContent = listOf<HelpContent>()
    private var filteredFAQs = listOf<FAQItem>()
    private var filteredHelpContent = listOf<HelpContent>()
    
    // Adapters
    private lateinit var faqAdapter: FAQAdapter
    private lateinit var helpContentAdapter: HelpContentAdapter
    
    private val TAG = "HelpAndFAQFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpAndFAQBinding.inflate(inflater, container, false)
        
        // Initialize dependencies
        tokenManager = TokenManager(requireContext())
        val db = AppDatabase.getDatabase(requireContext())
        helpContentDao = db.helpContentDao()
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        loadOfflineContent()
        updateSyncStatus()
    }
    
    private fun setupUI() {
        // Initialize adapters
        faqAdapter = FAQAdapter()
        helpContentAdapter = HelpContentAdapter()
        
        // Setup RecyclerViews
        binding.faqRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = faqAdapter
        }
        
        binding.helpContentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = helpContentAdapter
        }
        
        // Setup search functionality
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterContent(s.toString())
            }
        })
        
        // Setup tabs
        binding.contentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showFAQContent()
                    1 -> showGuideContent()
                    2 -> showContactContent()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // Setup update button
        binding.updateContentButton.setOnClickListener {
            updateContentFromServer()
        }
        
        // Initially show FAQ content
        showFAQContent()
    }
    
    private fun loadOfflineContent() {
        lifecycleScope.launch {
            try {
                // Load default content if database is empty
                val existingFAQs = helpContentDao.getFAQsByCategory("general")
                val existingGuides = helpContentDao.getHelpContentByCategory("guide")
                
                if (existingFAQs.isEmpty()) {
                    loadDefaultFAQs()
                }
                
                if (existingGuides.isEmpty()) {
                    loadDefaultGuides()
                }
                
                // Load current content
                currentFAQs = helpContentDao.getFAQsByCategory("general")
                currentHelpContent = helpContentDao.getHelpContentByCategory("guide")
                filteredFAQs = currentFAQs
                filteredHelpContent = currentHelpContent
                
                showFAQContent()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading offline content: ${e.message}", e)
                Toast.makeText(context, "Error loading content", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun loadDefaultFAQs() {
        val defaultFAQs = listOf(
            FAQItem(
                id = "faq_1",
                question = "Bagaimana cara menambahkan transaksi baru?",
                answer = "Untuk menambahkan transaksi baru:\n1. Buka halaman Transaksi\n2. Tap tombol '+' di pojok kanan bawah\n3. Isi detail transaksi (jumlah, kategori, tanggal)\n4. Tap 'Simpan'",
                category = "general",
                order = 1,
                isPopular = true
            ),
            FAQItem(
                id = "faq_2",
                question = "Bagaimana cara melihat laporan keuangan?",
                answer = "Laporan keuangan dapat dilihat di:\n1. Menu Laporan di halaman utama\n2. Pilih periode yang diinginkan\n3. Lihat grafik pendapatan vs pengeluaran\n4. Export ke PDF jika diperlukan",
                category = "general",
                order = 2,
                isPopular = true
            ),
            FAQItem(
                id = "faq_3",
                question = "Apakah data saya aman?",
                answer = "Ya, data Anda aman karena:\n• Disimpan secara lokal di perangkat\n• Dilindungi dengan enkripsi\n• Dapat dibackup secara aman\n• Tidak dibagikan tanpa izin",
                category = "general",
                order = 3,
                isPopular = true
            ),
            FAQItem(
                id = "faq_4",
                question = "Bagaimana cara mengatur kategori pengeluaran?",
                answer = "Untuk mengatur kategori:\n1. Buka menu Pengaturan\n2. Pilih 'Kelola Kategori'\n3. Tambah, edit, atau hapus kategori\n4. Atur warna dan ikon kategori",
                category = "general",
                order = 4
            ),
            FAQItem(
                id = "faq_5",
                question = "Bagaimana cara backup data?",
                answer = "Backup data dapat dilakukan melalui:\n1. Menu Pengaturan > Backup & Restore\n2. Pilih 'Backup Sekarang'\n3. Data akan disimpan di penyimpanan lokal\n4. Untuk backup cloud, login diperlukan",
                category = "general",
                order = 5
            )
        )
        
        helpContentDao.insertAllFAQs(defaultFAQs)
    }
    
    private suspend fun loadDefaultGuides() {
        val defaultGuides = listOf(
            HelpContent(
                id = "guide_1",
                title = "Panduan Memulai",
                content = "Selamat datang di Financial Planner!\n\nLangkah pertama:\n1. Buat profil pengguna Anda\n2. Atur kategori pendapatan dan pengeluaran\n3. Mulai mencatat transaksi harian\n4. Lihat laporan mingguan/bulanan\n\nTips: Catat transaksi setiap hari untuk hasil yang akurat.",
                category = "guide",
                order = 1
            ),
            HelpContent(
                id = "guide_2", 
                title = "Mengelola Anggaran",
                content = "Cara membuat anggaran yang efektif:\n\n1. Tetapkan limit untuk setiap kategori\n2. Monitor pengeluaran secara berkala\n3. Gunakan notifikasi peringatan\n4. Evaluasi dan sesuaikan anggaran bulanan\n\nAnggaran yang realistis adalah kunci kesuksesan finansial.",
                category = "guide",
                order = 2
            ),
            HelpContent(
                id = "guide_3",
                title = "Fitur Laporan",
                content = "Memahami laporan keuangan:\n\n• Grafik Pendapatan vs Pengeluaran\n• Breakdown per kategori\n• Trend bulanan/tahunan\n• Analisis cash flow\n\nGunakan filter tanggal untuk analisis periode tertentu.",
                category = "guide",
                order = 3
            )
        )
        
        helpContentDao.insertAllHelpContent(defaultGuides)
    }
    
    private fun showFAQContent() {
        // Hide all content views
        binding.faqRecyclerView.visibility = View.VISIBLE
        binding.helpContentRecyclerView.visibility = View.GONE
        binding.contactContentLayout.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        
        // Update adapter with filtered data
        faqAdapter.updateData(filteredFAQs)
        
        // Show empty state if no FAQs
        if (filteredFAQs.isEmpty()) {
            binding.faqRecyclerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        }
        
        updateUIForCurrentTab("FAQ")
    }
    
    private fun showGuideContent() {
        // Hide all content views
        binding.faqRecyclerView.visibility = View.GONE
        binding.helpContentRecyclerView.visibility = View.VISIBLE
        binding.contactContentLayout.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        
        // Update adapter with filtered data
        helpContentAdapter.updateData(filteredHelpContent)
        
        // Show empty state if no help content
        if (filteredHelpContent.isEmpty()) {
            binding.helpContentRecyclerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        }
        
        updateUIForCurrentTab("Panduan")
    }
    
    private fun showContactContent() {
        // Hide all content views
        binding.faqRecyclerView.visibility = View.GONE
        binding.helpContentRecyclerView.visibility = View.GONE
        binding.contactContentLayout.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        
        updateUIForCurrentTab("Kontak")
    }
    
    private fun updateUIForCurrentTab(tabName: String) {
        val count = when (tabName) {
            "FAQ" -> filteredFAQs.size
            "Panduan" -> filteredHelpContent.size
            else -> 0
        }
        
        Log.d(TAG, "Showing $tabName content: $count items")
    }
    
    private fun filterContent(query: String) {
        if (query.isBlank()) {
            filteredFAQs = currentFAQs
            filteredHelpContent = currentHelpContent
        } else {
            lifecycleScope.launch {
                try {
                    filteredFAQs = helpContentDao.searchFAQs(query)
                    filteredHelpContent = helpContentDao.searchHelpContent(query)
                    
                    // Update current view based on selected tab
                    val selectedTab = binding.contentTabLayout.selectedTabPosition
                    when (selectedTab) {
                        0 -> showFAQContent()
                        1 -> showGuideContent()
                        2 -> showContactContent()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error filtering content: ${e.message}", e)
                }
            }
        }
    }
    
    private fun updateSyncStatus() {
        lifecycleScope.launch {
            try {
                val isOnline = NetworkUtils.isNetworkAvailable(requireContext())
                val lastUpdate = helpContentDao.getFAQLastUpdateTime()
                
                binding.offlineIndicator.visibility = if (isOnline) View.GONE else View.VISIBLE
                
                if (lastUpdate != null) {
                    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                    val lastUpdateText = dateFormat.format(Date(lastUpdate))
                    binding.lastUpdateText.text = "Terakhir diperbarui: $lastUpdateText"
                } else {
                    binding.lastUpdateText.text = "Terakhir diperbarui: Belum pernah"
                }
                
                binding.syncStatusText.text = if (isOnline) {
                    "Konten dapat diperbarui"
                } else {
                    "Konten tersedia offline"
                }
                
                binding.syncStatusText.setTextColor(
                    if (isOnline) Color.GREEN else Color.parseColor("#FFA500")
                )
                
                binding.updateContentButton.isEnabled = isOnline
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating sync status: ${e.message}", e)
            }
        }
    }
    
    private fun updateContentFromServer() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.updateContentButton.isEnabled = false
                
                // Simulate API call delay
                kotlinx.coroutines.delay(2000)
                
                // For demo purposes, we'll just update the timestamp
                // In a real app, you would fetch content from your API
                
                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                val updateText = dateFormat.format(Date(currentTime))
                
                binding.lastUpdateText.text = "Terakhir diperbarui: $updateText"
                
                Toast.makeText(context, "Konten berhasil diperbarui", Toast.LENGTH_SHORT).show()
                
                // Reload content
                loadOfflineContent()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error updating content: ${e.message}", e)
                Toast.makeText(context, "Gagal memperbarui konten", Toast.LENGTH_SHORT).show()
            } finally {
                binding.loadingProgressBar.visibility = View.GONE
                updateSyncStatus()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateSyncStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}