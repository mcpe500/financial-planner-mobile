package com.example.financialplannerapp.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financialplannerapp.TokenManager

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenManager: TokenManager,
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