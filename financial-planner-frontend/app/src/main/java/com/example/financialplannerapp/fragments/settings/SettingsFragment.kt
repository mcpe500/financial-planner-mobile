package com.example.financialplannerapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // User Profile settings card
        binding.profileSettingsCard.setOnClickListener {
            Toast.makeText(context, "User profile coming soon", Toast.LENGTH_SHORT).show()
            // When navigation is ready:
            // findNavController().navigate(R.id.action_settingsFragment_to_userProfileFragment)
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
            Toast.makeText(context, "Data sync settings coming soon", Toast.LENGTH_SHORT).show()
        }

        // Backup & restore card
        binding.backupRestoreCard.setOnClickListener {
            Toast.makeText(context, "Backup & restore coming soon", Toast.LENGTH_SHORT).show()
        }

        // Help center card
        binding.helpCenterCard.setOnClickListener {
            Toast.makeText(context, "Help center coming soon", Toast.LENGTH_SHORT).show()
        }

        // Contact us card
        binding.contactUsCard.setOnClickListener {
            Toast.makeText(context, "Contact form coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}