package com.example.financialplannerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme

private const val TAG_MAIN_ACTIVITY = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tokenManager = TokenManager(this)

        setContent {
            FinancialPlannerAppTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        tokenManager = tokenManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        // Process initial intent in case of deep link launch
        intent?.let { handleDeepLinkIntent(it, tokenManager) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG_MAIN_ACTIVITY, "onNewIntent received: ${intent.dataString}")
        setIntent(intent) // Update the activity's intent so LoginScreen's LaunchedEffect can pick it up
        // Process the new intent
        handleDeepLinkIntent(intent, tokenManager)
    }

    private fun handleDeepLinkIntent(intent: Intent, tokenManager: TokenManager) {
        val data = intent.data
        if (data != null && "finplanner" == data.scheme) {
            val token = data.getQueryParameter("token")
            Log.d(TAG_MAIN_ACTIVITY, "MainActivity: Received token via deep link: $token")
            if (token != null) {
                tokenManager.saveToken(token)
                tokenManager.setNoAccountMode(false) 
                Log.d(TAG_MAIN_ACTIVITY, "Token saved. LoginScreen will handle navigation.")
            } else {
                Toast.makeText(this, "Authentication failed: No token received via deep link", Toast.LENGTH_SHORT).show()
            }
            // LoginScreen's LaunchedEffect, keyed on activity.intent, will handle the navigation.
            // To prevent re-processing if LoginScreen is already visible and receives the same intent again
            // before a new one arrives, LoginScreen itself should clear the intent data or use a ViewModel event.
            // For example, in LoginScreen's LaunchedEffect after handling the deep link:
            // (context as? Activity)?.intent?.data = null
        }
    }
}