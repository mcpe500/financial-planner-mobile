package com.example.financialplannerapp.fragments.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.api.RetrofitClient
import com.example.financialplannerapp.databinding.FragmentLoginBinding
import com.example.financialplannerapp.utils.TokenManager
import com.google.android.gms.common.SignInButton
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Debug log to verify the fragment is being created
        Log.d(TAG, "onCreateView called")
        
        // Initialize binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        // If user is already logged in, verify token and go to dashboard
        if (tokenManager.getToken() != null) {
            Log.d(TAG, "Found token, verifying and navigating")
            verifyTokenAndNavigate()
            return
        } else if (tokenManager.isNoAccountMode()) {
            // If in no-account mode, go directly to dashboard
            Log.d(TAG, "No-account mode enabled, navigating to dashboard")
            navigateToDashboard()
            return
        }

        // Debug logging to check if UI elements exist
        if (binding.signInButton != null) {
            Log.d(TAG, "signInButton found in binding")
        } else {
            Log.e(TAG, "signInButton is NULL in binding!")
        }
        
        // Set up Google Sign In button
        try {
            binding.signInButton.setSize(SignInButton.SIZE_WIDE)
            binding.signInButton.setOnClickListener {
                signInWithGoogle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up signInButton: ${e.message}")
        }

        // Set up Passcode button
        try {
            binding.passcodeButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_passcodeFragment)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up passcodeButton: ${e.message}")
        }

        // Set up No Account button
        try {
            binding.noAccountButton.setOnClickListener {
                Log.d(TAG, "No account button clicked")
                tokenManager.setNoAccountMode(true)
                navigateToDashboard()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up noAccountButton: ${e.message}")
        }

        // Handle deep link if the activity was started with a URL
        activity?.intent?.let { handleIntent(it) }
    }

    private fun verifyTokenAndNavigate() {
        // Show loading state if you have one
        try {
            binding.progressBar?.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.d(TAG, "No progress bar in layout: ${e.message}")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader != null) {
                    val response = RetrofitClient.authService.getCurrentUser(authHeader)
                    
                    if (response.isSuccessful) {
                        // Token is valid, navigate to dashboard
                        Log.d(TAG, "Token verified successfully")
                        navigateToDashboard()
                    } else {
                        // Token is invalid, clear it and stay on login screen
                        Log.e(TAG, "Token verification failed: ${response.code()}")
                        if (response.code() == 401) {
                            Toast.makeText(requireContext(), "Your session has expired. Please login again.", Toast.LENGTH_LONG).show()
                            tokenManager.clearToken()
                        }
                    }
                } else {
                    // No token, stay on login screen
                    Log.d(TAG, "No auth header available")
                }
            } catch (e: Exception) {
                // Network or other error
                Log.e(TAG, "Error verifying token: ${e.message}")
                Toast.makeText(requireContext(), "Could not verify login status. Please try again.", Toast.LENGTH_LONG).show()
            } finally {
                try {
                    // Hide loading state
                    binding.progressBar?.visibility = View.GONE
                } catch (e: Exception) {
                    Log.d(TAG, "No progress bar in layout: ${e.message}")
                }
            }
        }
    }

    private fun navigateToDashboard() {
        try {
            // Ensure this action exists in nav_graph.xml
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to dashboard: ${e.message}")
            Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun signInWithGoogle() {
        // Use RetrofitClient's BASE_URL from Config rather than hardcoding
        val googleAuthUrl = "${RetrofitClient.retrofit.baseUrl()}api/auth/google"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleAuthUrl))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Could not open browser for Google Sign-In", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Error starting Google Sign-In intent: ${e.message}")
        }
    }

    private fun handleIntent(intent: Intent) {
        val data = intent.data
        if (data != null && "finplanner" == data.scheme) {
            val token = data.getQueryParameter("token")
            Log.d(TAG, "Received token via deep link: $token")
            if (token != null) {
                tokenManager.saveToken(token)
                tokenManager.setNoAccountMode(false) // User is logged in, not in no-account mode
                
                // Verify the token with the API
                verifyTokenAndNavigate()
            } else {
                Toast.makeText(requireContext(), "Authentication failed: No token received", Toast.LENGTH_SHORT).show()
            }
            activity?.intent?.data = null // Clear the intent data to prevent re-processing
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}