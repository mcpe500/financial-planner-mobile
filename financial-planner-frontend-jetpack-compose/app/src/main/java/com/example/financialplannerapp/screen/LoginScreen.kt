package com.example.financialplannerapp.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview

private const val TAG_LOGIN_SCREEN = "LoginScreen"

@Composable
fun LoginScreen(navController: NavController, tokenManager: TokenManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    LaunchedEffect(key1 = tokenManager, key2 = activity?.intent) {
        Log.d(TAG_LOGIN_SCREEN, "LoginScreen LaunchedEffect triggered. Intent: ${activity?.intent?.dataString}")
        val currentIntent = activity?.intent
        var deepLinkHandled = false

        if (currentIntent?.data != null && "finplanner" == currentIntent.data?.scheme) {
            handleIntent(currentIntent, tokenManager, navController, { isLoading = it }, context, scope)
            deepLinkHandled = true
            try {
                activity?.intent?.data = null
            } catch (e: Exception) {
                Log.w(TAG_LOGIN_SCREEN, "Could not clear intent data: ${e.message}")
            }
        }

        if (!deepLinkHandled) {
            if (tokenManager.getToken() != null) {
                Log.d(TAG_LOGIN_SCREEN, "Token found, verifying...")
                verifyTokenAndNavigate(tokenManager, navController, { isLoading = it }, context, scope)
            } else if (tokenManager.isNoAccountMode()) {
                Log.d(TAG_LOGIN_SCREEN, "No-account mode active, navigating to dashboard.")
                navigateToDashboard(navController)
            } else {
                Log.d(TAG_LOGIN_SCREEN, "No token or no-account mode. User needs to login.")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()        } else {
            Text(
                text = "Welcome to Financial Planner", 
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 100.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                signInWithGoogle(context)
            }) {
                Text("Sign in with Google")
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                Log.d(TAG_LOGIN_SCREEN, "Passcode button clicked")
                navController.navigate("passcode")
            }) {
                Text("Use Passcode")
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                Log.d(TAG_LOGIN_SCREEN, "Continue Without Account button clicked")
                tokenManager.setNoAccountMode(true)
                navigateToDashboard(navController)
            }) {
                Text("Continue Without Account")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent()
}

@Composable
private fun LoginScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Financial Planner", 
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 100.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { /* Preview - no action */ }) {
            Text("Sign in with Google")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { /* Preview - no action */ }) {
            Text("Use Passcode")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { /* Preview - no action */ }) {
            Text("Continue Without Account")
        }
    }
}

// Helper functions for LoginScreen
private fun handleIntent(
    intent: Intent,
    tokenManager: TokenManager,
    navController: NavController,
    setIsLoading: (Boolean) -> Unit,
    context: Context,
    scope: CoroutineScope
) {
    val data = intent.data
    if (data != null && "finplanner" == data.scheme) {
        val token = data.getQueryParameter("token")
        Log.d(TAG_LOGIN_SCREEN, "handleIntent: Received token via deep link: $token")
        if (token != null) {
            tokenManager.saveToken(token)
            tokenManager.setNoAccountMode(false)
            verifyTokenAndNavigate(tokenManager, navController, setIsLoading, context, scope)
        } else {
            Toast.makeText(context, "Authentication failed: No token received in deep link", Toast.LENGTH_SHORT).show()
            Log.e(TAG_LOGIN_SCREEN, "Deep link token is null")
        }
    }
}

private fun verifyTokenAndNavigate(
    tokenManager: TokenManager,
    navController: NavController,
    setIsLoading: (Boolean) -> Unit,
    context: Context,
    scope: CoroutineScope
) {
    setIsLoading(true)
    scope.launch {
        try {
            val authHeader = tokenManager.getAuthHeader()
            if (authHeader != null) {
                Log.d(TAG_LOGIN_SCREEN, "Verifying token with auth header: $authHeader")
                
                val response = RetrofitClient.authService.getCurrentUser(authHeader)
                
                if (response.isSuccessful) {
                    // Token is valid, save user info and navigate to dashboard
                    response.body()?.let { user ->
                        tokenManager.saveUserInfo(user.id, user.email, user.name)
                        Log.d(TAG_LOGIN_SCREEN, "Token verified successfully for user: ${user.name}")
                    }
                    navigateToDashboard(navController)
                } else {
                    // Token is invalid, clear it and stay on login screen
                    Log.e(TAG_LOGIN_SCREEN, "Token verification failed: ${response.code()}")
                    if (response.code() == 401) {
                        Toast.makeText(context, "Your session has expired. Please login again.", Toast.LENGTH_LONG).show()
                        tokenManager.clearToken()
                    }
                }
            } else {
                // No token, stay on login screen
                Log.d(TAG_LOGIN_SCREEN, "No auth header available")
            }
        } catch (e: Exception) {
            // Network or other error
            Log.e(TAG_LOGIN_SCREEN, "Error verifying token: ${e.message}", e)
            Toast.makeText(context, "Could not verify login status. Please try again.", Toast.LENGTH_LONG).show()
        } finally {
            setIsLoading(false)
        }
    }
}

private fun navigateToDashboard(navController: NavController) {
    Log.d(TAG_LOGIN_SCREEN, "Navigating to dashboard")
    navController.navigate("dashboard") {
        popUpTo("login") { inclusive = true }
        launchSingleTop = true
    }
}

private fun signInWithGoogle(context: Context) {
    // Use RetrofitClient's BASE_URL from Config rather than hardcoding
    val googleAuthUrl = "${RetrofitClient.retrofit.baseUrl()}api/auth/google"
    Log.d(TAG_LOGIN_SCREEN, "Attempting to sign in with Google: $googleAuthUrl")
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleAuthUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open browser for Google Sign-In", Toast.LENGTH_LONG).show()
        Log.e(TAG_LOGIN_SCREEN, "Error starting Google Sign-In intent: ${e.message}", e)
    }
}
