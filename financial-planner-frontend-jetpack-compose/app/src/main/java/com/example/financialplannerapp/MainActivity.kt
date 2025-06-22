package com.example.financialplannerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.service.AppProvider
import com.example.financialplannerapp.ui.navigation.AppNavigation
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodel.AuthViewModel
import com.example.financialplannerapp.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private var intentUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        tokenManager = TokenManager(this)
        
        // Handle intent data
        handleIntent(intent)
        
        setContent {
            AppProvider {
                FinancialPlannerAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppContent()
                    }
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
        // Obtain application instance and container
        val application = LocalContext.current.applicationContext as MainApplication
        val authViewModel: AuthViewModel = viewModel(
            factory = AuthViewModelFactory(application.appContainer.authRepository)
        )
        // similarly obtain other ViewModels using their factories and repositories
        
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