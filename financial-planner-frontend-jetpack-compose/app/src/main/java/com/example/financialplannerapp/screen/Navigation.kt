package com.example.financialplannerapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.financialplannerapp.screen.LoginScreen
import com.example.financialplannerapp.screen.DashboardScreen
import com.example.financialplannerapp.screen.PasscodeScreen

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
