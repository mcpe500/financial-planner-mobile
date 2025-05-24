package com.example.financialplannerapp.fragments.settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentAccountDeletionBinding
import com.example.financialplannerapp.network.ApiResponse
import com.example.financialplannerapp.services.AccountDeletionService
import com.example.financialplannerapp.utils.PreferenceManager
import kotlinx.coroutines.launch

class AccountDeletionFragment : Fragment() {
    
    private var _binding: FragmentAccountDeletionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var accountDeletionService: AccountDeletionService
    private lateinit var preferenceManager: PreferenceManager
    
    private var currentStep = AccountDeletionStep.INITIAL_WARNING
    private var deletionToken: String? = null
    private var verificationToken: String? = null
    private var userEmail: String? = null
    
    enum class AccountDeletionStep {
        INITIAL_WARNING,
        EMAIL_VERIFICATION_SENT,
        OTP_VERIFICATION,
        FINAL_CONFIRMATION,
        DELETION_COMPLETE
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDeletionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        accountDeletionService = AccountDeletionService()
        preferenceManager = PreferenceManager(requireContext())
        userEmail = preferenceManager.getUserEmail()
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        updateUIForStep(currentStep)
    }
    
    private fun updateUIForStep(step: AccountDeletionStep) {
        currentStep = step
        
        // Hide all views first
        binding.apply {
            layoutInitialWarning.visibility = View.GONE
            layoutEmailVerification.visibility = View.GONE
            layoutOtpVerification.visibility = View.GONE
            layoutFinalConfirmation.visibility = View.GONE
            layoutDeletionComplete.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        
        when (step) {
            AccountDeletionStep.INITIAL_WARNING -> {
                binding.layoutInitialWarning.visibility = View.VISIBLE
                binding.textUserEmail.text = userEmail
            }
            
            AccountDeletionStep.EMAIL_VERIFICATION_SENT -> {
                binding.layoutEmailVerification.visibility = View.VISIBLE
                binding.textEmailSentTo.text = getString(R.string.email_sent_to, userEmail)
            }
            
            AccountDeletionStep.OTP_VERIFICATION -> {
                binding.layoutOtpVerification.visibility = View.VISIBLE
                setupOtpInput()
            }
            
            AccountDeletionStep.FINAL_CONFIRMATION -> {
                binding.layoutFinalConfirmation.visibility = View.VISIBLE
                binding.textConfirmationEmail.text = userEmail
                setupEmailConfirmationInput()
            }
            
            AccountDeletionStep.DELETION_COMPLETE -> {
                binding.layoutDeletionComplete.visibility = View.VISIBLE
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.apply {
            // Initial warning step
            btnStartDeletion.setOnClickListener {
                showFinalWarningDialog()
            }
            
            btnCancel.setOnClickListener {
                requireActivity().onBackPressed()
            }
            
            // Email verification step
            btnOpenEmailApp.setOnClickListener {
                openEmailApp()
            }
            
            btnVerifyOtp.setOnClickListener {
                updateUIForStep(AccountDeletionStep.OTP_VERIFICATION)
            }
            
            // OTP verification step
            btnSubmitOtp.setOnClickListener {
                verifyOtp()
            }
            
            btnResendEmail.setOnClickListener {
                requestAccountDeletion()
            }
            
            // Final confirmation step
            btnConfirmDeletion.setOnClickListener {
                confirmAccountDeletion()
            }
            
            btnCancelDeletion.setOnClickListener {
                requireActivity().onBackPressed()
            }
            
            // Back buttons
            btnBackFromEmail.setOnClickListener {
                updateUIForStep(AccountDeletionStep.INITIAL_WARNING)
            }
            
            btnBackFromOtp.setOnClickListener {
                updateUIForStep(AccountDeletionStep.EMAIL_VERIFICATION_SENT)
            }
            
            btnBackFromConfirmation.setOnClickListener {
                updateUIForStep(AccountDeletionStep.OTP_VERIFICATION)
            }
        }
    }
    
    private fun setupOtpInput() {
        binding.editTextOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.btnSubmitOtp.isEnabled = s?.length == 6
            }
        })
    }
    
    private fun setupEmailConfirmationInput() {
        binding.editTextConfirmEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val matches = s.toString().trim().equals(userEmail, ignoreCase = true)
                binding.btnConfirmDeletion.isEnabled = matches
                
                if (s?.isNotEmpty() == true && !matches) {
                    binding.textLayoutConfirmEmail.error = getString(R.string.email_does_not_match)
                } else {
                    binding.textLayoutConfirmEmail.error = null
                }
            }
        })
    }
    
    private fun showFinalWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_account_warning_title)
            .setMessage(R.string.delete_account_warning_message)
            .setPositiveButton(R.string.yes_delete_account) { _, _ ->
                requestAccountDeletion()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun requestAccountDeletion() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val authToken = preferenceManager.getAuthToken()
            if (authToken.isNullOrEmpty()) {
                Toast.makeText(requireContext(), R.string.authentication_required, Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
                return@launch
            }
            
            when (val response = accountDeletionService.requestAccountDeletion(authToken)) {
                is ApiResponse.Success -> {
                    deletionToken = response.data.token
                    binding.progressBar.visibility = View.GONE
                    updateUIForStep(AccountDeletionStep.EMAIL_VERIFICATION_SENT)
                    Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_SHORT).show()
                }
                
                is ApiResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun verifyOtp() {
        val otp = binding.editTextOtp.text.toString().trim()
        val token = deletionToken
        
        if (otp.length != 6) {
            Toast.makeText(requireContext(), R.string.enter_valid_otp, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.invalid_session, Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            when (val response = accountDeletionService.verifyOtp(token, otp)) {
                is ApiResponse.Success -> {
                    verificationToken = response.data.verificationToken
                    binding.progressBar.visibility = View.GONE
                    updateUIForStep(AccountDeletionStep.FINAL_CONFIRMATION)
                    Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_SHORT).show()
                }
                
                is ApiResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.editTextOtp.error = response.message
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun confirmAccountDeletion() {
        val confirmationEmail = binding.editTextConfirmEmail.text.toString().trim()
        val token = verificationToken
        
        if (!confirmationEmail.equals(userEmail, ignoreCase = true)) {
            Toast.makeText(requireContext(), R.string.email_does_not_match, Toast.LENGTH_SHORT).show()
            return
        }
        
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.invalid_session, Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            when (val response = accountDeletionService.confirmDeletion(token, confirmationEmail)) {
                is ApiResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    updateUIForStep(AccountDeletionStep.DELETION_COMPLETE)
                    
                    // Clear user data and redirect to login
                    preferenceManager.clearAllData()
                    
                    Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_LONG).show()
                    
                    // Redirect to login activity after a delay
                    binding.root.postDelayed({
                        // Navigate to login activity
                        requireActivity().finish()
                    }, 3000)
                }
                
                is ApiResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun openEmailApp() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to Gmail or other email apps
            try {
                val gmailIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://gmail.com")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(gmailIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.no_email_app_found, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}