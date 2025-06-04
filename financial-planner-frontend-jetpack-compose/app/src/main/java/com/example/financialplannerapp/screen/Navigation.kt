package com.example.financialplannerapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.screen.auth.LoginScreen
import com.example.financialplannerapp.screen.dashboard.DashboardScreen
import com.example.financialplannerapp.screen.passcode.PasscodeScreen

private const val TAG_LOGIN_SCREEN = "LoginScreen"

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenManager: TokenManager, // Pass TokenManager if screens need it directly
    modifier: Modifier = Modifier
) {
    val startDestination = "login"

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable("login") {
            LoginScreen(navController = navController, tokenManager = tokenManager)
        }
        composable("dashboard") {
            DashboardScreen(navController = navController, tokenManager = tokenManager)
        }
        composable("passcode") { 
            PasscodeScreen(navController = navController)
        }
    }
}
