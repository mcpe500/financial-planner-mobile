package com.example.financialplannerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.screen.*

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
        composable("settingsScreen") {
            SettingsScreen(navController = navController)
        }
        
        // Settings Sub-screens
        composable("userProfileSettings") {
            UserProfileSettingsScreen(
                navController = navController,
                tokenManager = tokenManager
            )
        }
        
        composable("securitySettings") {
            SecuritySettingsScreen(navController = navController)
        }
        
        composable("appSettings") {
            AppSettingsScreen(navController = navController)
        }
        
        composable("dataSyncSettings") {
            DataSyncSettingsScreen(navController = navController)
        }
        
        composable("backupRestoreSettings") {
            BackupRestoreSettingsScreen(navController = navController)
        }
        
        composable("helpCenterSettings") {
            HelpCenterScreen(navController = navController)
        }
        
        composable("contactSupportSettings") {
            ContactSupportScreen(navController = navController)
        }
    }
}