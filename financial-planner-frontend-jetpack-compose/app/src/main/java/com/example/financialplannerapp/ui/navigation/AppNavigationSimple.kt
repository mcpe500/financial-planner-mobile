package com.example.financialplannerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.ui.screens.auth.LoginScreen
import com.example.financialplannerapp.ui.screens.auth.PasscodeScreen
import com.example.financialplannerapp.ui.screens.main.DashboardScreen
import com.example.financialplannerapp.ui.screens.settings.SettingsScreen
import com.example.financialplannerapp.ui.screens.security.PinBiometricScreen
import com.example.financialplannerapp.ui.screens.profile.UserProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Passcode : Screen("passcode")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object UserProfile : Screen("userProfile")
    object PinBiometric : Screen("pinBiometric")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Handle login logic
                    navController.navigate(Screen.Dashboard.route)
                },
                onRegisterClick = {
                    // Navigate to register screen
                },
                onForgotPasswordClick = {
                    // Navigate to forgot password screen
                }
            )
        }
        
        composable(Screen.Passcode.route) {
            PasscodeScreen(
                onPasscodeEntered = { passcode ->
                    // Handle passcode validation
                    navController.navigate(Screen.Dashboard.route)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = {
                    // Navigate to transactions
                },
                onNavigateToGoals = {
                    // Navigate to goals
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.UserProfile.route)
                },
                onNavigateToSecurity = {
                    navController.navigate(Screen.PinBiometric.route)
                },
                onLogout = {
                    // Handle logout
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.UserProfile.route) {
            UserProfileScreen(
                onSaveProfile = { name, email ->
                    // Handle save profile
                    navController.popBackStack()
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.PinBiometric.route) {
            PinBiometricScreen(
                onPinSetup = {
                    // Handle PIN setup
                },
                onBiometricSetup = {
                    // Handle biometric setup
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}