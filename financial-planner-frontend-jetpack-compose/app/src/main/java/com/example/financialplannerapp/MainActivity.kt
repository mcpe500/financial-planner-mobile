// <!-- filepath: app/src/main/java/com/example/financialplannerapp/MainActivity.kt -->
package com.example.financialplannerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.ui.navigation.AppNavigation
import com.example.financialplannerapp.ui.navigation.Screen
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme
import com.example.financialplannerapp.ui.viewmodels.MainViewModel // Create this ViewModel
import com.example.financialplannerapp.utils.TokenManager

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        mainViewModel = ViewModelProvider(this, MainViewModel.Factory(tokenManager, application))[MainViewModel::class.java]


        setContent {
            FinancialPlannerAppTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                var showLogoutButton by remember { mutableStateOf(false) }
                showLogoutButton = currentRoute != null &&
                        currentRoute != Screen.Login.route &&
                        !currentRoute.startsWith(Screen.Passcode.route) // Assuming Passcode is also a pre-login screen


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (showLogoutButton) {
                            Button(
                                onClick = {
                                    mainViewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.padding(16.dp) // Adjust padding as needed
                            ) {
                                Text("Logout")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(
                            navController = navController,
                            tokenManager = tokenManager,
                            mainViewModel = mainViewModel,
                            onAuthSuccessNavigateToDashboard = {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToPasscode = {
                                navController.navigate(Screen.Passcode.route)
                            }
                        )
                    }
                }
            }
        }
        handleIntent(intent) // Handle initial intent
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the activity's intent
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "finplanner" && uri.host == "auth") {
                val token = uri.getQueryParameter("token")
                if (token != null) {
                    Log.d("MainActivity", "Received token via deep link: $token")
                    mainViewModel.handleDeepLinkToken(token)
                    // Navigation will be handled by AppNavigation observing MainViewModel state
                } else {
                    Log.e("MainActivity", "Deep link missing token")
                }
                // Clear the intent data after processing to prevent re-processing on config change
                // For Activities, this is typically done, but for Compose, ensure ViewModel handles it once.
                // Consider if `intent.data = null` is needed or if ViewModel logic suffices.
                // For now, let ViewModel manage the one-time event.
                 this.intent.data = null // Clear intent data
            }
        }
    }
}