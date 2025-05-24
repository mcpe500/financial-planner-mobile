// <!-- filepath: app/src/main/java/com/example/financialplannerapp/ui/navigation/AppNavigation.kt -->
package com.example.financialplannerapp.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.financialplannerapp.ui.screens.auth.LoginScreen
import com.example.financialplannerapp.ui.screens.auth.PasscodeScreen
import com.example.financialplannerapp.ui.screens.dashboard.DashboardScreen
import com.example.financialplannerapp.ui.screens.settings.HelpAndFaqScreen
import com.example.financialplannerapp.ui.screens.settings.PinBiometricScreen
import com.example.financialplannerapp.ui.screens.settings.SettingsScreen
import com.example.financialplannerapp.ui.screens.settings.UserProfileScreen
import com.example.financialplannerapp.ui.viewmodels.MainViewModel
import com.example.financialplannerapp.ui.viewmodels.auth.LoginViewModel
import com.example.financialplannerapp.ui.viewmodels.auth.PasscodeViewModel
import com.example.financialplannerapp.ui.viewmodels.dashboard.DashboardViewModel
import com.example.financialplannerapp.ui.viewmodels.settings.HelpFaqViewModel
import com.example.financialplannerapp.ui.viewmodels.settings.PinBiometricViewModel
import com.example.financialplannerapp.ui.viewmodels.settings.SettingsViewModel
import com.example.financialplannerapp.ui.viewmodels.settings.UserProfileViewModel
import com.example.financialplannerapp.utils.TokenManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Passcode : Screen("passcode")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object UserProfile : Screen("userProfile")
    object PinBiometric : Screen("pinBiometric")
    object HelpAndFaq : Screen("helpAndFaq")
    // Add other screens: AppSettings, DataSync, BackupRestore
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    tokenManager: TokenManager,
    mainViewModel: MainViewModel,
    onAuthSuccessNavigateToDashboard: () -> Unit,
    onNavigateToPasscode: () -> Unit
) {
    val application = navController.context.applicationContext as Application
    val deepLinkAuthState by mainViewModel.deepLinkAuthState.collectAsState()

    LaunchedEffect(deepLinkAuthState) {
        if (deepLinkAuthState == MainViewModel.DeepLinkAuthState.AUTHENTICATED) {
            onAuthSuccessNavigateToDashboard()
            mainViewModel.resetDeepLinkState() // Reset state after navigation
        }
    }

    val startDestination = if (tokenManager.getToken() != null || tokenManager.isNoAccountMode()) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(tokenManager, application)
            )
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel,
                onLoginSuccess = onAuthSuccessNavigateToDashboard,
                onNavigateToPasscode = onNavigateToPasscode
            )
        }
        composable(Screen.Passcode.route) {
             val passcodeViewModel: PasscodeViewModel = viewModel(
                factory = PasscodeViewModel.Factory(application)
            )
            PasscodeScreen(
                navController = navController,
                viewModel = passcodeViewModel,
                onPasscodeSuccess = onAuthSuccessNavigateToDashboard
            )
        }
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(tokenManager, application)
            )
            DashboardScreen(navController = navController, viewModel = dashboardViewModel)
        }
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(tokenManager, application)
            )
            SettingsScreen(navController = navController, viewModel = settingsViewModel)
        }
        composable(Screen.UserProfile.route) {
            val userProfileViewModel: UserProfileViewModel = viewModel(
                factory = UserProfileViewModel.Factory(tokenManager, application)
            )
            UserProfileScreen(navController = navController, viewModel = userProfileViewModel)
        }
        composable(Screen.PinBiometric.route) {
            val pinBioViewModel: PinBiometricViewModel = viewModel(
                factory = PinBiometricViewModel.Factory(application)
            )
            PinBiometricScreen(navController = navController, viewModel = pinBioViewModel)
        }
        composable(Screen.HelpAndFaq.route) {
            val helpFaqViewModel: HelpFaqViewModel = viewModel(
                factory = HelpFaqViewModel.Factory(application)
            )
            HelpAndFaqScreen(navController = navController, viewModel = helpFaqViewModel)
        }
        // Add other composable destinations here
    }
}