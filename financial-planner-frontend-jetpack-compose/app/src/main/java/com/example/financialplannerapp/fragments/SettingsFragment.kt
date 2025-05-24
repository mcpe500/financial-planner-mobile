package com.example.financialplannerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.FragmentSettingsBinding
import com.example.financialplannerapp.ui.activities.AuthActivity
import com.example.financialplannerapp.ui.viewmodels.ViewModelFactory
import com.example.financialplannerapp.ui.viewmodels.auth.AuthViewModel
import com.example.financialplannerapp.ui.viewmodels.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireActivity().application) }
    private val settingsViewModel: SettingsViewModel by viewModels { ViewModelFactory(requireActivity().application) }

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
        observeViewModels()

        if (settingsViewModel.isNoAccountMode()) {
            configureForNoAccountMode()
        } else {
            settingsViewModel.loadUserProfile()
        }
    }

    private fun configureForNoAccountMode() {
        binding.textViewUserName.text = getString(R.string.guest_user)
        binding.textViewUserEmail.text = getString(R.string.using_app_in_no_account_mode)
        Glide.with(this)
            .load(R.drawable.ic_profile_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imageViewProfile)
        binding.buttonLogout.text = getString(R.string.login_or_register)
        // Disable or hide options not available in no-account mode
        binding.linearLayoutProfile.isClickable = false
        binding.linearLayoutAccount.isClickable = false
        binding.linearLayoutNotifications.visibility = View.GONE
        binding.linearLayoutSecurity.visibility = View.GONE
        binding.linearLayoutDeleteAccount.visibility = View.GONE
        // Add more UI changes as needed
    }

    private fun setupClickListeners() {        binding.buttonLogout.setOnClickListener {
            if (settingsViewModel.isNoAccountMode()) {
                authViewModel.exitNoAccountModeAndGoToLogin()
            } else {
                showLogoutConfirmationDialog()
            }
        }
        
        binding.linearLayoutProfile.setOnClickListener {
            if (!settingsViewModel.isNoAccountMode()) {
                // TODO: Implement navigation when nav graph is ready
                Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.linearLayoutAccount.setOnClickListener {
            if (!settingsViewModel.isNoAccountMode()) {
                // Navigate to Account Settings (e.g., change password, email - if applicable)
                // findNavController().navigate(R.id.action_settingsFragment_to_accountSettingsFragment)
                Toast.makeText(context, "Account settings clicked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.linearLayoutNotifications.setOnClickListener {
            if (!settingsViewModel.isNoAccountMode()) {
                // Navigate to Notification Settings
                // findNavController().navigate(R.id.action_settingsFragment_to_notificationSettingsFragment)
                Toast.makeText(context, "Notification settings clicked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.linearLayoutSecurity.setOnClickListener {
            if (!settingsViewModel.isNoAccountMode()) {
                // Navigate to Security Settings (e.g., PIN, Biometrics)
                // findNavController().navigate(R.id.action_settingsFragment_to_securitySettingsFragment)
                Toast.makeText(context, "Security settings clicked", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.linearLayoutHelp.setOnClickListener {
            // TODO: Implement navigation when nav graph is ready
            Toast.makeText(context, "Help & FAQ clicked", Toast.LENGTH_SHORT).show()
        }
        binding.linearLayoutContactUs.setOnClickListener {
            // TODO: Implement navigation when nav graph is ready
            Toast.makeText(context, "Contact Us clicked", Toast.LENGTH_SHORT).show()
        }
        binding.linearLayoutDeleteAccount.setOnClickListener {
             if (!settingsViewModel.isNoAccountMode()) {
                // TODO: Implement navigation when nav graph is ready
                Toast.makeText(context, "Account Deletion clicked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.linearLayoutAbout.setOnClickListener {
            // Navigate to About screen or show a dialog
            // findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
            Toast.makeText(context, "About clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModels() {
        settingsViewModel.userProfile.observe(viewLifecycleOwner, Observer { userProfile ->
            if (!settingsViewModel.isNoAccountMode()) {
                userProfile?.let {
                    binding.textViewUserName.text = it.name ?: getString(R.string.not_set)
                    binding.textViewUserEmail.text = it.email ?: getString(R.string.not_set)
                    Glide.with(this)
                        .load(it.profileImageUrl ?: R.drawable.ic_profile_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder) // Ensure ic_profile_placeholder is a valid drawable
                        .into(binding.imageViewProfile)
                } ?: run {
                    // Handle case where user profile is null but not in no-account mode (e.g., after failed load)
                    binding.textViewUserName.text = getString(R.string.error_loading_profile)
                    binding.textViewUserEmail.text = ""
                    Glide.with(this)
                        .load(R.drawable.ic_profile_placeholder)
                        .apply(RequestOptions.circleCropTransform())                        .into(binding.imageViewProfile)
                }
            }
        })

        settingsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide a progress bar if you have one
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        settingsViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                settingsViewModel.clearError()
            }
        }

        settingsViewModel.logoutState.observe(viewLifecycleOwner) { loggedOut ->
            if (loggedOut) {
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                navigateToAuthActivity()
                settingsViewModel.resetLogoutState() // Reset state after handling
            }
        }

        // Observe AuthViewModel for navigation from no-account mode
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            if (state == com.example.financialplannerapp.ui.viewmodels.auth.AuthState.UNAUTHENTICATED && settingsViewModel.isNoAccountMode()) {
                 // This condition means we exited no-account mode and should go to AuthActivity
                navigateToAuthActivity()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                settingsViewModel.logout()
            }
            .show()
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}