package com.example.financialplannerapp.fragments.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.api.RetrofitClient
import com.example.financialplannerapp.databinding.FragmentContactUsReportBinding
import com.example.financialplannerapp.utils.NetworkUtils
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContactUsReportFragment : Fragment() {
    private var _binding: FragmentContactUsReportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var tokenManager: TokenManager
    private var isSubmitting = false
    
    private val TAG = "ContactUsReportFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsReportBinding.inflate(inflater, container, false)
        
        tokenManager = TokenManager(requireContext())
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadUserInfo()
        updateNetworkStatus()
    }

    private fun setupUI() {
        // Setup back button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Setup priority spinner
        val priorityOptions = arrayOf(
            "Rendah - Pertanyaan umum",
            "Sedang - Masalah fungsionalitas",
            "Tinggi - Bug yang mengganggu",
            "Kritis - Aplikasi tidak bisa digunakan"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            priorityOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prioritySpinner.adapter = adapter
        
        // Setup character counter
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val count = s?.length ?: 0
                binding.characterCountText.text = "$count/1000"
                
                // Change color if approaching limit
                if (count > 900) {
                    binding.characterCountText.setTextColor(resources.getColor(R.color.red_error, null))
                } else if (count > 700) {
                    binding.characterCountText.setTextColor(resources.getColor(R.color.yellow_warning, null))
                } else {
                    binding.characterCountText.setTextColor(resources.getColor(android.R.color.tertiary_text_dark, null))
                }
                
                // Limit character count
                if (count > 1000) {
                    s?.delete(1000, count)
                }
            }
        })
        
        // Setup send button
        binding.sendButton.setOnClickListener {
            if (validateForm()) {
                submitFeedback()
            }
        }
        
        // Monitor network status
        lifecycleScope.launch {
            while (true) {
                updateNetworkStatus()
                delay(5000) // Check every 5 seconds
            }
        }
    }
    
    private fun loadUserInfo() {
        lifecycleScope.launch {
            try {
                val userId = tokenManager.getUserId() ?: "guest_user"
                val email = tokenManager.getUserEmail() ?: "user@example.com"
                
                binding.userIdText.text = userId
                binding.userEmailText.text = email
                
            } catch (e: Exception) {
                // Handle error silently
                binding.userIdText.text = "Unknown"
                binding.userEmailText.text = "user@example.com"
            }
        }
    }
    
    private fun updateNetworkStatus() {
        val isOnline = NetworkUtils.isNetworkAvailable(requireContext())
        
        if (isOnline) {
            binding.networkStatusIndicator.text = "Online"
            binding.networkStatusIndicator.backgroundTintList = 
                resources.getColorStateList(R.color.green_success, null)
            binding.offlineNoticeCard.visibility = View.GONE
            binding.sendButton.isEnabled = true
        } else {
            binding.networkStatusIndicator.text = "Offline"
            binding.networkStatusIndicator.backgroundTintList = 
                resources.getColorStateList(R.color.red_error, null)
            binding.offlineNoticeCard.visibility = View.VISIBLE
            binding.sendButton.isEnabled = false
        }
    }
    
    private fun validateForm(): Boolean {
        val subject = binding.subjectEditText.text.toString().trim()
        val message = binding.messageEditText.text.toString().trim()
        
        if (subject.isEmpty()) {
            binding.subjectEditText.error = "Subjek tidak boleh kosong"
            binding.subjectEditText.requestFocus()
            return false
        }
        
        if (subject.length < 10) {
            binding.subjectEditText.error = "Subjek minimal 10 karakter"
            binding.subjectEditText.requestFocus()
            return false
        }
        
        if (message.isEmpty()) {
            binding.messageEditText.error = "Pesan tidak boleh kosong"
            binding.messageEditText.requestFocus()
            return false
        }
        
        if (message.length < 20) {
            binding.messageEditText.error = "Pesan minimal 20 karakter"
            binding.messageEditText.requestFocus()
            return false
        }
        
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "Tidak ada koneksi internet. Pesan akan dikirim ketika koneksi tersedia.", Toast.LENGTH_LONG).show()
            return false
        }
        
        return true
    }
    
    private fun submitFeedback() {
        if (isSubmitting) return
        
        isSubmitting = true
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val contactType = getSelectedContactType()
                val subject = binding.subjectEditText.text.toString().trim()
                val message = binding.messageEditText.text.toString().trim()
                val priority = binding.prioritySpinner.selectedItem.toString()
                val userId = tokenManager.getUserId() ?: "guest_user"
                val userEmail = tokenManager.getUserEmail() ?: "user@example.com"
                
                // Create feedback data
                val feedbackData = mapOf(
                    "type" to contactType,
                    "subject" to subject,
                    "message" to message,
                    "priority" to priority,
                    "userId" to userId,
                    "userEmail" to userEmail,
                    "timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date()),
                    "appVersion" to "1.0.0",
                    "deviceInfo" to "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                )
                
                // Simulate API call (replace with actual API call)
                simulateApiCall(feedbackData)
                
                // Show success message
                showSuccessMessage()
                
                // Clear form
                clearForm()
                
            } catch (e: HttpException) {
                handleApiError(e)
            } catch (e: Exception) {
                handleGenericError(e)
            } finally {
                isSubmitting = false
                showLoading(false)
            }
        }
    }
    
    private suspend fun simulateApiCall(feedbackData: Map<String, String>) {
        // Simulate network delay
        delay(2000)
        
        // For now, we'll just simulate a successful response
        // In a real implementation, you would call your backend API here
        // Example:
        // val response = RetrofitClient.apiService.submitFeedback(feedbackData)
        
        // Simulate random success/failure for demonstration
        if (Math.random() > 0.1) { // 90% success rate
            // Success - do nothing, will proceed to success message
        } else {
            throw Exception("Simulated server error")
        }
    }
    
    private fun getSelectedContactType(): String {
        return when (binding.contactTypeRadioGroup.checkedRadioButtonId) {
            R.id.feedbackRadioButton -> "feedback"
            R.id.bugReportRadioButton -> "bug_report"
            R.id.supportRadioButton -> "technical_support"
            R.id.featureRequestRadioButton -> "feature_request"
            else -> "feedback"
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.visibility = View.VISIBLE
            binding.sendButton.visibility = View.GONE
        } else {
            binding.loadingLayout.visibility = View.GONE
            binding.sendButton.visibility = View.VISIBLE
        }
    }
    
    private fun showSuccessMessage() {
        Toast.makeText(
            context,
            "Pesan berhasil dikirim! Tim dukungan akan menghubungi Anda dalam 1-2 hari kerja.",
            Toast.LENGTH_LONG
        ).show()
        
        // Optionally navigate back or show success screen
        findNavController().navigateUp()
    }
    
    private fun clearForm() {
        binding.subjectEditText.text?.clear()
        binding.messageEditText.text?.clear()
        binding.contactTypeRadioGroup.check(R.id.feedbackRadioButton)
        binding.prioritySpinner.setSelection(0)
    }
    
    private fun handleApiError(e: HttpException) {
        val errorMessage = when (e.code()) {
            400 -> "Data yang dikirim tidak valid. Periksa kembali form Anda."
            401 -> "Sesi telah berakhir. Silakan login kembali."
            403 -> "Akses ditolak. Hubungi administrator."
            404 -> "Service tidak ditemukan. Coba lagi nanti."
            429 -> "Terlalu banyak permintaan. Coba lagi dalam beberapa menit."
            500 -> "Terjadi kesalahan server. Tim teknis telah diberitahu."
            else -> "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
        }
        
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }
    
    private fun handleGenericError(e: Exception) {
        val errorMessage = when {
            e.message?.contains("timeout", ignoreCase = true) == true -> 
                "Koneksi timeout. Periksa keterangan internet Anda."
            e.message?.contains("network", ignoreCase = true) == true -> 
                "Masalah koneksi jaringan. Coba lagi nanti."
            else -> "Terjadi kesalahan tak terduga. Coba lagi nanti."
        }
        
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        updateNetworkStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}