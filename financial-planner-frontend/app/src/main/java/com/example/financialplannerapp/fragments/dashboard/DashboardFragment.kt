package com.example.financialplannerapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.activities.MainActivity
import com.example.financialplannerapp.databinding.FragmentDashboardBinding
import com.example.financialplannerapp.utils.TokenManager

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show logout button in MainActivity
        (activity as? MainActivity)?.showLogoutButton()

        // Set welcome message based on login state
        if (tokenManager.isNoAccountMode()) {
            binding.welcomeText.text = "Dashboard (Guest Mode)"
        } else {
            binding.welcomeText.text = "Welcome to Your Dashboard"
            // You can fetch user data here if needed
        }

        // Initialize dashboard UI components
        setupDashboardUI()
    }

    private fun setupDashboardUI() {
        // Set up any dashboard-specific UI components here
        // For example:
        // binding.addTransactionButton.setOnClickListener { /* Navigate to add transaction screen */ }

        // Settings button navigation
        binding.settingsButton.setOnClickListener {
            navigateToSettings()
        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}