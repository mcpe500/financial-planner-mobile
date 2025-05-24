package com.example.financialplannerapp.fragments.settings
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentPinBiometricBinding

class PinBiometricFragment : Fragment() {
    private var _binding: FragmentPinBiometricBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    
    companion object {
        private const val PREFS_NAME = "SecurityPrefs"
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_PIN_VALUE = "pin_value"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBiometricBinding.inflate(inflater, container, false)
        sharedPrefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupBiometricAuthentication()
        loadSettings()
    }
    
    private fun setupUI() {
        // Set up the PIN switch
        binding.pinSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.pinInputLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.confirmPinInputLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            
            if (!isChecked) {
                // Clear PIN fields when disabling
                binding.pinInputEditText.text?.clear()
                binding.confirmPinInputEditText.text?.clear()
                
                // Save the disabled state
                sharedPrefs.edit()
                    .putBoolean(KEY_PIN_ENABLED, false)
                    .putString(KEY_PIN_VALUE, null)
                    .apply()
                
                Toast.makeText(context, "PIN dinonaktifkan", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Set up the Biometric switch
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBiometricAvailability()
            } else {
                // Save the disabled state
                sharedPrefs.edit()
                    .putBoolean(KEY_BIOMETRIC_ENABLED, false)
                    .apply()
                
                Toast.makeText(context, "Biometrik dinonaktifkan", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Set up the save button for PIN
        binding.saveButton.setOnClickListener {
            savePin()
        }
        
        // Show authentication options availability
        checkBiometricAvailability(showToast = false)
    }
    
    private fun setupBiometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Save biometric enabled state
                    sharedPrefs.edit()
                        .putBoolean(KEY_BIOMETRIC_ENABLED, true)
                        .apply()
                    
                    Toast.makeText(
                        context,
                        "Autentikasi biometrik berhasil diaktifkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    
                    // Revert the switch if authentication failed or was canceled
                    binding.biometricSwitch.isChecked = false
                    
                    Toast.makeText(
                        context,
                        "Error autentikasi: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context,
                        "Autentikasi gagal, coba lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Aktifkan autentikasi biometrik")
            .setSubtitle("Konfirmasi identitas Anda untuk mengaktifkan fitur biometrik")
            .setNegativeButtonText("Batal")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }
    
    private fun loadSettings() {
        // Load saved settings
        val pinEnabled = sharedPrefs.getBoolean(KEY_PIN_ENABLED, false)
        val biometricEnabled = sharedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        
        // Update UI based on saved settings
        binding.pinSwitch.isChecked = pinEnabled
        binding.pinInputLayout.visibility = if (pinEnabled) View.VISIBLE else View.GONE
        binding.confirmPinInputLayout.visibility = if (pinEnabled) View.GONE else View.GONE
        
        binding.biometricSwitch.isChecked = biometricEnabled
    }
    
    private fun checkBiometricAvailability(showToast: Boolean = true) {
        val biometricManager = BiometricManager.from(requireContext())
        
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (binding.biometricSwitch.isChecked) {
                    // When enabling, show the biometric prompt
                    biometricPrompt.authenticate(promptInfo)
                }
                binding.biometricStatusText.text = "Biometrik tersedia"
                try {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                } catch (e: Exception) {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.biometricSwitch.isChecked = false
                binding.biometricSwitch.isEnabled = false
                binding.biometricStatusText.text = "Perangkat ini tidak mendukung biometrik"
                try {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                } catch (e: Exception) {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
                if (showToast) {
                    Toast.makeText(context, "Perangkat ini tidak mendukung biometrik", Toast.LENGTH_SHORT).show()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.biometricSwitch.isChecked = false
                binding.biometricSwitch.isEnabled = false
                binding.biometricStatusText.text = "Biometrik tidak tersedia saat ini"
                try {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                } catch (e: Exception) {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
                if (showToast) {
                    Toast.makeText(context, "Biometrik tidak tersedia saat ini", Toast.LENGTH_SHORT).show()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.biometricSwitch.isChecked = false
                binding.biometricStatusText.text = "Tidak ada biometrik yang terdaftar di perangkat"
                try {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
                } catch (e: Exception) {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
                if (showToast) {
                    Toast.makeText(
                        context,
                        "Daftarkan setidaknya satu biometrik di pengaturan perangkat",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {
                binding.biometricSwitch.isChecked = false
                binding.biometricSwitch.isEnabled = false
                binding.biometricStatusText.text = "Status biometrik tidak diketahui"
                try {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                } catch (e: Exception) {
                    binding.biometricStatusText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }
            }
        }
    }
    
    private fun savePin() {
        val pin = binding.pinInputEditText.text.toString()
        val confirmPin = binding.confirmPinInputEditText.text.toString()
        
        if (pin.isEmpty()) {
            binding.pinInputLayout.error = "PIN tidak boleh kosong"
            return
        }
        
        if (pin.length < 4) {
            binding.pinInputLayout.error = "PIN minimal 4 digit"
            return
        }
        
        // If we're setting a new PIN, check confirmation
        if (binding.confirmPinInputLayout.visibility == View.VISIBLE) {
            if (confirmPin.isEmpty()) {
                binding.confirmPinInputLayout.error = "Masukkan konfirmasi PIN"
                return
            }
            
            if (pin != confirmPin) {
                binding.confirmPinInputLayout.error = "PIN dan konfirmasi PIN tidak sama"
                return
            }
        }
        
        // Clear any error messages
        binding.pinInputLayout.error = null
        binding.confirmPinInputLayout.error = null
        
        // Save the PIN
        sharedPrefs.edit()
            .putBoolean(KEY_PIN_ENABLED, true)
            .putString(KEY_PIN_VALUE, pin)
            .apply()
        
        // Hide the confirm field after successful save
        binding.confirmPinInputLayout.visibility = View.GONE
        
        // Clear fields
        binding.pinInputEditText.text?.clear()
        binding.confirmPinInputEditText.text?.clear()
        
        Toast.makeText(context, "PIN berhasil disimpan", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}