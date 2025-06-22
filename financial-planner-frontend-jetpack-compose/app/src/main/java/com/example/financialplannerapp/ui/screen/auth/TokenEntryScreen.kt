package com.example.financialplannerapp.ui.screen.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.remote.RetrofitClient
import com.example.financialplannerapp.data.model.LoginResponse
import kotlinx.coroutines.launch

private const val TAG_TOKEN_ENTRY = "TokenEntryScreen"

@Composable
fun TokenEntryScreen(navController: NavController, tokenManager: TokenManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var token by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Authentication Token",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Paste the token you copied from the browser:",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token") },
            placeholder = { Text("Paste your token here...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            maxLines = 3
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = {
                    if (token.isNotBlank()) {
                        verifyAndSaveToken(token.trim(), tokenManager, navController, context, scope) { isLoading = it }
                    } else {
                        Toast.makeText(context, "Please enter a token", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = token.isNotBlank()
            ) {
                Text("Verify Token")
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}

private fun verifyAndSaveToken(
    token: String,
    tokenManager: TokenManager,
    navController: NavController,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    setIsLoading: (Boolean) -> Unit
) {
    setIsLoading(true)
    scope.launch {
        try {
            // Save token temporarily
            tokenManager.saveToken(token)
            
            // Verify token with backend
            val apiService = RetrofitClient.getApiService(context)
            val authHeader = tokenManager.getAuthHeader()
            if (authHeader != null) {
                Log.d(TAG_TOKEN_ENTRY, "Verifying token...")
                val response = apiService.getCurrentUser(authHeader)
                
                if (response.isSuccessful) {
                    // Token is valid, save user info
                    response.body()?.let { userResponse ->
                        userResponse.user?.let { user ->
                            tokenManager.saveUserInfo(user.id, user.email, user.name)
                            tokenManager.setNoAccountMode(false)
                            Log.d(TAG_TOKEN_ENTRY, "Token verified successfully for user: ${user.name}")
                            
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        } ?: run {
                            Toast.makeText(context, "Invalid user data received", Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Invalid response from server", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Token is invalid
                    Log.e(TAG_TOKEN_ENTRY, "Token verification failed: ${response.code()}")
                    Toast.makeText(context, "Invalid token. Please try again.", Toast.LENGTH_LONG).show()
                    tokenManager.clearToken()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG_TOKEN_ENTRY, "Error verifying token: ${e.message}", e)
            Toast.makeText(context, "Network error. Please check your connection.", Toast.LENGTH_LONG).show()
            tokenManager.clearToken()
        } finally {
            setIsLoading(false)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TokenEntryScreenPreview() {
    var token by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Authentication Token",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Paste the token you copied from the browser:",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token") },
            placeholder = { Text("Paste your token here...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            maxLines = 3
        )

        Button(
            onClick = { /* Preview - no action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Verify Token")
        }

        Button(
            onClick = { /* Preview - no action */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}