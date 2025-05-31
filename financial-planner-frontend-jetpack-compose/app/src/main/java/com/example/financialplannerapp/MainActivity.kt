package com.example.financialplannerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.navigation.AppNavigation
import com.example.financialplannerapp.service.AppProvider

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private var intentUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        tokenManager = TokenManager(this)
        
        // Handle intent data
        handleIntent(intent)
        
        setContent {
            AppProvider {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d("MainActivity", "Handling intent: ${intent.action}, data: ${intent.data}")
        intentUri = intent.data
    }

    @Composable
    private fun AppContent() {
        val navController = rememberNavController()
        
        // Determine start destination based on login state
        val startDestination = if (tokenManager.getToken() != null || tokenManager.isNoAccountMode()) {
            "dashboard"
        } else {
            "login"
        }
        
        Log.d("MainActivity", "Start destination: $startDestination")
        
        AppNavigation(
            navController = navController,
            tokenManager = tokenManager,
            startDestination = startDestination
        )
    }
}