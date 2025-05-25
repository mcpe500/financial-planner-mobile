package com.example.financialplannerapp.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financialplannerapp.TokenManager

@Composable
fun DashboardScreen(navController: NavController, tokenManager: TokenManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Screen")
        Button(onClick = {
            Log.d("DashboardScreen", "Logout button clicked")
            tokenManager.clear()
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }
                launchSingleTop = true
            }
        }) {
            Text("Logout")
        }
    }
}