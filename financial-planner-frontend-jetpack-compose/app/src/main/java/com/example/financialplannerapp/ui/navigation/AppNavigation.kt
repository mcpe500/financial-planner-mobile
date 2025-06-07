package com.example.financialplannerapp.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.ui.screen.auth.LoginScreen
import com.example.financialplannerapp.ui.screen.dashboard.DashboardScreen
import com.example.financialplannerapp.ui.screen.settings.AppSettingsScreen
import com.example.financialplannerapp.ui.screen.settings.SettingsScreen
import com.example.financialplannerapp.screen.settings.UserProfileSettingsScreen
import com.example.financialplannerapp.screen.settings.SecuritySettingsScreen
import com.example.financialplannerapp.screen.settings.DataSyncSettingsScreen
import com.example.financialplannerapp.screen.settings.BackupRestoreSettingsScreen
import com.example.financialplannerapp.screen.settings.ContactSupportScreen
import com.example.financialplannerapp.screen.settings.HelpCenterScreen
import com.example.financialplannerapp.screen.AddTransactionScreen
import com.example.financialplannerapp.screen.ScanReceiptScreen
import com.example.financialplannerapp.screen.TransactionHistoryScreen
import com.example.financialplannerapp.screen.VoiceInputScreen
import com.example.financialplannerapp.ui.screen.transaction.TransactionMainScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenManager: TokenManager,
    startDestination: String = "login"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screen
        composable("login") {
            LoginScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        
        // Main Dashboard
        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        
        // Main Settings Screen
        composable("settings") {
            AppSettingsScreen(
                onNavigateToPersonalProfile = { navController.navigate("personal_profile") },
                onNavigateToSecurity = { navController.navigate("security") },
                onNavigateToAppInfo = { navController.navigate("app_info") },
                onLogout = { 
                    // Handle logout logic
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        
        // Settings Sub-screens
        composable("personal_profile") {
            UserProfileSettingsScreen(
                navController = navController, 
                tokenManager = tokenManager
            )
        }
        
        composable("security") {
            SecuritySettingsScreen(
                navController = navController, 
                tokenManager = tokenManager
            )
        }
        
        composable("app_info") {
            // Create a simple app info screen or navigate to existing one
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("App Information Screen - Coming Soon")
            }
        }
        
        composable("data_sync") {
            DataSyncSettingsScreen(navController = navController)
        }
        
        composable("backup_restore") {
            BackupRestoreSettingsScreen(navController = navController)
        }
        
        composable("help_center") {
            HelpCenterScreen(navController = navController)
        }
        
        composable("contact_support") {
            ContactSupportScreen(navController = navController)
        }
        
        // Transaction Screens
        composable("transactions") {
            TransactionMainScreen(navController = navController)
        }
        
        composable("add_transaction") {
            AddTransactionScreen(navController = navController)
        }
        
        composable("transaction_history") {
            TransactionHistoryScreen(navController = navController)
        }
        
        composable("scan_receipt") {
            ScanReceiptScreen(navController = navController)
        }
        
        composable("voice_input") {
            VoiceInputScreen(navController = navController)
        }
    }
}