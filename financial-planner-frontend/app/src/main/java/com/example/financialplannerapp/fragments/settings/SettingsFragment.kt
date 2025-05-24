package com.example.financialplannerapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up User Profile settings card
        binding.profileSettingsCard.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_userProfileFragment)
        }

        // Security settings card
        binding.securitySettingsCard.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_settingsFragment_to_pinBiometricFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // App settings card
        binding.appSettingsCard.setOnClickListener {
            Toast.makeText(context, "App settings coming soon", Toast.LENGTH_SHORT).show()
        }

        // Data sync card
        binding.dataSyncCard.setOnClickListener {
            Toast.makeText(context, "Data sync coming soon", Toast.LENGTH_SHORT).show()
        }

        // Backup & restore card
        binding.backupRestoreCard.setOnClickListener {
            Toast.makeText(context, "Backup & restore coming soon", Toast.LENGTH_SHORT).show()
        }

        // Help center card
        binding.helpCenterCard.setOnClickListener {
            try {
                // For now, show a toast until navigation is properly set up
                // Toast.makeText(context, "Help center implementation complete - navigation setup needed", Toast.LENGTH_LONG).show()
                // Uncomment when navigation action is added:
                findNavController().navigate(R.id.action_settingsFragment_to_helpAndFAQFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Invalid argument: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalStateException) {
                Toast.makeText(context, "Illegal state: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Contact us card
        binding.contactUsCard.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_settingsFragment_to_contactUsReportFragment)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateStatusIndicators() {
        lifecycleScope.launch {
            try {
                // Update profile sync status
                val userId = tokenManager.getUserId() ?: "guest_user"
                val profile = userProfileDao.getUserProfile(userId)
                val profileNeedsSync = profile?.needsSync == true
                
                // Update network status
                val isOnline = NetworkUtils.isNetworkAvailable(requireContext())
                
                // Update security status
                val securityPrefs = requireContext().getSharedPreferences("SecurityPrefs", Context.MODE_PRIVATE)
                val pinEnabled = securityPrefs.getBoolean("pin_enabled", false)
                val biometricEnabled = securityPrefs.getBoolean("biometric_enabled", false)
                
                // Update Profile Settings status
                if (profileNeedsSync) {
                    binding.profileSettingsDescription.text = "Perubahan belum disinkronkan"
                    binding.profileSettingsDescription.setTextColor(resources.getColor(R.color.yellow_warning, null))
                } else {
                    binding.profileSettingsDescription.text = "Lihat dan ubah profil pengguna Anda"
                    binding.profileSettingsDescription.setTextColor(resources.getColor(android.R.color.tertiary_text_dark, null))
                }
                
                // Update Security Settings status
                val securityStatus = when {
                    pinEnabled && biometricEnabled -> "PIN dan Biometrik aktif"
                    pinEnabled -> "PIN aktif"
                    biometricEnabled -> "Biometrik aktif"
                    else -> "Belum dikonfigurasi"
                }
                
                binding.securitySettingsDescription.text = securityStatus
                binding.securitySettingsDescription.setTextColor(
                    if (pinEnabled || biometricEnabled) 
                        resources.getColor(R.color.green_success, null) 
                    else 
                        resources.getColor(R.color.red_error, null)
                )
                
            } catch (e: Exception) {
                // Handle error silently to avoid UI disruption
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusIndicators()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}