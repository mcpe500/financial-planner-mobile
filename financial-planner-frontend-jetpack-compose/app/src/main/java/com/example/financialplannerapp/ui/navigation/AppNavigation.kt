package com.example.financialplannerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.ui.screen.auth.LoginScreen
import com.example.financialplannerapp.ui.screen.dashboard.DashboardScreen
import com.example.financialplannerapp.ui.screen.settings.AppSettingsScreen
import com.example.financialplannerapp.ui.screen.settings.BackupRestoreSettingsScreen
import com.example.financialplannerapp.ui.screen.settings.ContactSupportScreen
import com.example.financialplannerapp.ui.screen.settings.DataSyncSettingsScreen
import com.example.financialplannerapp.ui.screen.settings.HelpCenterScreen
import com.example.financialplannerapp.ui.screen.settings.SecuritySettingsScreen
import com.example.financialplannerapp.ui.screen.settings.SettingsScreen
import com.example.financialplannerapp.ui.screen.settings.UserProfileSettingsScreen

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
            UserProfileSettingsScreen(navController = navController, tokenManager = tokenManager)
        }
        
        composable("securitySettings") {
            SecuritySettingsScreen(navController = navController, tokenManager = tokenManager)
        }
        
        composable("appSettings") {
            AppSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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