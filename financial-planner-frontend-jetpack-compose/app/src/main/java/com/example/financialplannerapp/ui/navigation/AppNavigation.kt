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
import com.example.financialplannerapp.ui.screen.settings.SettingsScreen // Assuming this is your individual settings screen composable
import com.example.financialplannerapp.screen.settings.UserProfileSettingsScreen
import com.example.financialplannerapp.screen.settings.SecuritySettingsScreen
import com.example.financialplannerapp.screen.settings.DataSyncSettingsScreen
import com.example.financialplannerapp.screen.settings.BackupRestoreSettingsScreen
import com.example.financialplannerapp.screen.settings.ContactSupportScreen
import com.example.financialplannerapp.screen.settings.HelpCenterScreen
import com.example.financialplannerapp.ui.screen.transaction.AddTransactionScreen
import com.example.financialplannerapp.ui.screen.transaction.ScanReceiptScreen
import com.example.financialplannerapp.ui.screen.transaction.TransactionHistoryScreen
import com.example.financialplannerapp.screen.VoiceInputScreen
import com.example.financialplannerapp.ui.screen.bill.AddRecurringBillScreen
import com.example.financialplannerapp.ui.screen.bill.BillCalendarScreen
import com.example.financialplannerapp.ui.screen.bill.RecurringBillsMainScreen
import com.example.financialplannerapp.ui.screen.transaction.TransactionMainScreen
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.financialplannerapp.screen.AddWalletScreen
import com.example.financialplannerapp.screen.DebtReceivableMainScreen
import com.example.financialplannerapp.screen.WalletsMainScreen

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
                    tokenManager.clear() // Clear token on logout
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
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

        composable("bills") {
            RecurringBillsMainScreen(navController = navController)
        }

        composable("add_bill") {
            AddRecurringBillScreen(navController = navController)
        }
        composable("bill_calendar") {
            BillCalendarScreen(navController = navController)
        }
        composable("bill_details/{billId}") { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId")
            AddRecurringBillScreen(navController = navController, billId = billId)
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

        // --- New Quick Action Routes ---
        composable("wallet") {
            WalletsMainScreen(navController = navController)
        }

        composable("add_wallet") {
            AddWalletScreen(navController = navController)
        }
        composable("debt") {
            DebtReceivableMainScreen()
        }
        composable("goals") {
            PlaceholderScreen(title = "Financial Goals Screen")
        }
        // --- End New Quick Action Routes ---
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "This is the $title", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Content for $title will be implemented here.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}