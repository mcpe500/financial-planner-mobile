package com.example.financialplannerapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.ActivityAuthBinding
import com.example.financialplannerapp.ui.viewmodels.ViewModelFactory
import com.example.financialplannerapp.ui.viewmodels.auth.AuthState
import com.example.financialplannerapp.ui.viewmodels.auth.AuthState
import com.example.financialplannerapp.utils.TokenManager
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(application)
    }
    private lateinit var tokenManager: TokenManager

    companion object {
        private const val TAG = "AuthActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        Log.d(TAG, "onCreate: AuthActivity started.")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        navController = navHostFragment.navController

        // The start destination is defined in nav_graph_auth.xml (OnboardingFragment)
        // If we wanted to conditionally set the start destination (e.g., skip onboarding):
        // val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_auth)
        // if (authViewModel.hasCompletedOnboarding()) { // Assuming such a method exists
        //     navGraph.setStartDestination(R.id.loginFragment)
        // } else {
        //     navGraph.setStartDestination(R.id.onboardingFragment)
        // }
        // navController.graph = navGraph
        // For now, we use the default start destination from the XML.

        observeAuthState()

        // Check initial auth state (e.g., if valid token exists)
        // This might trigger an immediate navigation to MainActivity if already authenticated.
        // The AuthViewModel's init block should handle checking the initial token status.
        // authViewModel.checkInitialAuthState() // Call this if not handled in init or if needed on activity recreation
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.authState.collect { state ->
                    Log.d(TAG, "AuthState changed: $state")
                    binding.progressBarAuth.visibility = if (state == AuthState.LOADING) View.VISIBLE else View.GONE
                    when (state) {
                        AuthState.AUTHENTICATED, AuthState.GUEST -> {
                            Log.d(TAG, "Navigating to MainActivity.")
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                            finish()
                        }
                        AuthState.UNAUTHENTICATED -> {
                            Log.d(TAG, "User is unauthenticated. Auth flow will continue.")
                            // Navigation within AuthActivity is handled by NavController
                            // and nav_graph_auth.xml
                        }
                        AuthState.LOADING -> {
                            Log.d(TAG, "Auth state is LOADING.")
                        }
                        AuthState.IDLE -> {
                             Log.d(TAG, "Auth state is IDLE.")
                        }
                        AuthState.ERROR -> {
                            Log.d(TAG, "Auth state is ERROR.")
                        }
                        AuthState.NO_ACCOUNT_MODE -> {
                            Log.d(TAG, "Auth state is NO_ACCOUNT_MODE.")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.error.collect { errorMessage ->
                    errorMessage?.let {
                        Log.e(TAG, "Auth Error: $it")
                        // Optionally show a Snackbar or Toast for critical errors not handled by fragments
                        // Toast.makeText(this@AuthActivity, it, Toast.LENGTH_LONG).show()
                        // authViewModel.clearError() // Clear error after showing
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called with intent: $intent")
        // Handle deep links if AuthActivity is the target
        // For example, if finplanner://auth is meant to be handled here
        // navController.handleDeepLink(intent)
    }
}