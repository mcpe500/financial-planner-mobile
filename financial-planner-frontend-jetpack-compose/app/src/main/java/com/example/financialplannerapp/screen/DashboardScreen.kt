package com.example.financialplannerapp.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.financialplannerapp.TokenManager

private const val TAG_DASHBOARD_SCREEN = "DashboardScreen"

@Composable
fun DashboardScreen(navController: NavController, tokenManager: TokenManager) {
    Log.d(TAG_DASHBOARD_SCREEN, "DashboardScreen composing...")
    
    val userName = tokenManager.getUserName() ?: "Guest"
    val userEmail = tokenManager.getUserEmail() ?: "No email"
    val isLoggedIn = tokenManager.getToken() != null
    val isNoAccountMode = tokenManager.isNoAccountMode()
    
    Log.d(TAG_DASHBOARD_SCREEN, "User info - Name: $userName, Email: $userEmail, LoggedIn: $isLoggedIn, NoAccountMode: $isNoAccountMode")
    
    DashboardContent(
        userName = userName,
        userEmail = userEmail,
        isLoggedIn = isLoggedIn,
        isNoAccountMode = isNoAccountMode,
        onLogout = {
            tokenManager.clear()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    )
}

@Composable
private fun DashboardContent(
    userName: String = "Guest",
    userEmail: String = "No email",
    isLoggedIn: Boolean = false,
    isNoAccountMode: Boolean = false,
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Financial Planner Dashboard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (isNoAccountMode) "Guest Mode" else "Logged in as:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLoggedIn && !isNoAccountMode) {
                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isNoAccountMode) {
                    Text(
                        text = "Using app without account",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Account Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Balance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$1,234.56",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoggedIn || isNoAccountMode) "Logout" else "Back to Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardContent(
        userName = "John Doe",
        userEmail = "john.doe@example.com",
        isLoggedIn = true,
        isNoAccountMode = false
    )
}