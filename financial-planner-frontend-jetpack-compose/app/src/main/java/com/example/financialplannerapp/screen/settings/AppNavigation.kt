package com.example.financialplannerapp.screen.settings

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.screen.dashboard.DashboardScreen
import com.example.financialplannerapp.screen.passcode.PasscodeScreen
import com.example.financialplannerapp.screen.auth.LoginScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val hasToken = tokenManager.getToken() != null
    val isNoAccountMode = tokenManager.isNoAccountMode()
    
    // Start with login, but deep link handling in LoginScreen will navigate to dashboard
    val startDestination = if (hasToken || isNoAccountMode) {
        Log.d("AppNavigation", "User already authenticated, starting with dashboard")
        "dashboard"
    } else {
        Log.d("AppNavigation", "No authentication, starting with login")
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable("login") {
            Log.d("AppNavigation", "=== COMPOSING LOGIN SCREEN ===")
            LoginScreen(navController = navController, tokenManager = tokenManager)
        }
        composable(
            route = "dashboard",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "finplanner://auth?token={token}&userId={userId}&email={email}&name={name}"
                }
            )
        ) { backStackEntry ->
            // Extract deep link parameters if present
            backStackEntry.arguments?.let { args ->
                val token = args.getString("token")
                val userId = args.getString("userId")
                val email = args.getString("email")
                val name = args.getString("name")
                if (token != null && userId != null) {
                    tokenManager.saveToken(token)
                    tokenManager.saveUserInfo(userId, email, name)
                }
            }
            Log.d("AppNavigation", "=== COMPOSING DASHBOARD SCREEN ===")
            DashboardScreen(navController = navController, tokenManager = tokenManager)
        }
        composable("passcode") {
            Log.d("AppNavigation", "=== COMPOSING PASSCODE SCREEN ===")
            PasscodeScreen(navController = navController)
        }
    }
}