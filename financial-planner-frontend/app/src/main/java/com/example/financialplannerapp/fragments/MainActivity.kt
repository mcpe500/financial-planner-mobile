package com.example.financialplannerapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.financialplannerapp.R
import com.example.financialplannerapp.databinding.ActivityMainBinding
import com.example.financialplannerapp.fragments.auth.LoginFragment
import com.example.financialplannerapp.utils.TokenManager
import com.example.financialplannerapp.utils.SecurityUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var tokenManager: TokenManager
    private lateinit var navController: NavController
    private val TAG = "MainActivity"
    private const val AUTH_TIMEOUT_MILLIS = 30 * 1000L // 30 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TokenManager
        tokenManager = TokenManager(this)
        
        // Set up Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // IMPORTANT: Set the navigation graph programmatically
        navController.setGraph(R.navigation.nav_graph)

        // Initialize app state based on user auth status
        setupNavigation(savedInstanceState)
        
        // Set up logout button
        binding.logoutButton.setOnClickListener {
            Log.d(TAG, "Logout button clicked")
            tokenManager.clear() // Clear all preferences including token and no-account mode
            navController.navigate(R.id.loginFragment, null, androidx.navigation.NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, true)
                .build())
            hideLogoutButton()
        }
    }

    // Add savedInstanceState as a parameter
    private fun setupNavigation(savedInstanceState: Bundle?) {
        // Add destination change listener to control logout button visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG, "Navigation: Navigated to ${destination.label}")
            
            // Show logout button except on login screen
            if (destination.id == R.id.loginFragment) {
                hideLogoutButton()
            } else {
                showLogoutButton()
            }
        }
        
        // Check if we need to redirect the user based on login state
        if (savedInstanceState == null) { // Only on fresh start, not configuration change
            // Don't explicitly navigate at startup - let the nav graph handle initial destination
            if (tokenManager.getToken() != null || tokenManager.isNoAccountMode()) {
                // User is authenticated or in no-account mode, go directly to dashboard
                Log.d(TAG, "User authenticated or in no-account mode, navigating to dashboard")
                
                // Use post to ensure navigation happens after the graph is fully set up
                binding.root.post {
                    navController.navigate(R.id.dashboardFragment, null, androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build())
                }
            }
            // No need for the else case - startDestination in nav_graph.xml will handle it
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent received: ${intent.dataString}")
        setIntent(intent) // Update the activity's intent
        
        // Handle deep links for auth
        if (intent.data != null && "finplanner" == intent.data?.scheme) {
            // Find and pass to LoginFragment if visible
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
                if (fragment is LoginFragment && fragment.isVisible) {
                    fragment.handleNewIntent(intent)
                    return
                }
            }
            
            // If LoginFragment not visible, navigate there then it will handle the intent
            binding.root.post {
                navController.navigate(R.id.loginFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun showLogoutButton() {
        binding.logoutButton.visibility = View.VISIBLE
    }

    fun hideLogoutButton() {
        binding.logoutButton.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called. Checking if auth is required.")
        if (SecurityUtils.isAuthRequired(this, AUTH_TIMEOUT_MILLIS)) {
            Log.d(TAG, "Authentication is required. Checking current destination.")
            val currentDestinationId = navController.currentDestination?.id
            Log.d(TAG, "Current destination ID: $currentDestinationId")

            // Prevent navigation if already on an auth screen or no destination yet
            if (currentDestinationId != null &&
                currentDestinationId != R.id.passcodeFragment &&
                currentDestinationId != R.id.loginFragment) {
                
                Log.d(TAG, "Navigating to PasscodeFragment as auth is required and not on auth screen.")
                // Ensure PasscodeFragment is reachable, e.g., via a global action
                // or as a top-level destination in your nav_graph.
                // This assumes R.id.passcodeFragment exists and is navigable from current location.
                try {
                    navController.navigate(R.id.passcodeFragment)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to navigate to PasscodeFragment. Ensure it's in nav_graph and reachable.", e)
                    Toast.makeText(this, "Error navigating to security screen.", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.d(TAG, "Not navigating to PasscodeFragment: currentDestinationId is null, or already on PasscodeFragment/LoginFragment.")
            }
        } else {
            Log.d(TAG, "Authentication is not required or PIN lock is disabled.")
        }
    }
}