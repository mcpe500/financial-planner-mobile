package com.example.financialplannerapp.ui.screen.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.R
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.config.Config
import com.example.financialplannerapp.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme

private const val TAG_LOGIN_SCREEN = "LoginScreen"

@Composable
fun LoginScreen(navController: NavController, tokenManager: TokenManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // State to hold the URI string from the intent, to trigger processing.
    // Initialize with the data from the intent when LoginScreen is first composed.
    var intentUriToProcess by remember {
        val activity = context as? Activity
        val currentData = activity?.intent?.dataString
        Log.d(TAG_LOGIN_SCREEN, "Initial intentUriToProcess set to: '$currentData'")
        mutableStateOf(currentData)
    }

    // Monitor for intent changes and process deep links
    LaunchedEffect(Unit) {
        val activity = context as? Activity
        if (activity != null) {
            // Check the current intent data immediately
            val currentIntentData = activity.intent?.dataString
            Log.d(TAG_LOGIN_SCREEN, "Checking current intent data: '$currentIntentData'")

            if (currentIntentData != null && currentIntentData.contains("finplanner://")) {
                Log.d(TAG_LOGIN_SCREEN, "Found deep link data immediately: '$currentIntentData'")
                intentUriToProcess = currentIntentData
            } else {
                // No deep link, check existing authentication
                Log.d(TAG_LOGIN_SCREEN, "No deep link found, checking existing token...")
                if (tokenManager.getToken() != null) {
                    Log.d(TAG_LOGIN_SCREEN, "Existing token found, verifying...")
                    verifyTokenAndNavigate(tokenManager, navController, { isLoading = it }, context, scope)
                } else if (tokenManager.isNoAccountMode()) {
                    Log.d(TAG_LOGIN_SCREEN, "No-account mode active, navigating to dashboard.")
                    navigateToDashboard(navController)
                }
            }
        }
    }

    // Also monitor for intent changes using snapshotFlow as backup
    LaunchedEffect(Unit) {
        snapshotFlow { (context as? Activity)?.intent?.dataString }
            .distinctUntilChanged()
            .collect { newIntentDataString ->
                Log.d(TAG_LOGIN_SCREEN, "snapshotFlow detected intent change: '$newIntentDataString'")
                if (newIntentDataString != null && newIntentDataString.contains("finplanner://")) {
                    Log.d(TAG_LOGIN_SCREEN, "Found deep link via snapshotFlow: '$newIntentDataString'")
                    intentUriToProcess = newIntentDataString
                }
            }
    }

    // Add a periodic check for deep links as fallback
    LaunchedEffect(Unit) {
        while (true) {
            delay(500) // Check every 500ms
            val activity = context as? Activity
            val currentIntentData = activity?.intent?.dataString
            if (currentIntentData != null && currentIntentData.contains("finplanner://") && intentUriToProcess != currentIntentData) {
                Log.d(TAG_LOGIN_SCREEN, "Periodic check found deep link: '$currentIntentData'")
                intentUriToProcess = currentIntentData
                break // Stop the periodic check once we find a deep link
            }
        }
    }

    // This effect reacts to changes in `intentUriToProcess`.
    LaunchedEffect(intentUriToProcess) {
        Log.d(TAG_LOGIN_SCREEN, "LaunchedEffect (key: intentUriToProcess) processing URI: '$intentUriToProcess'")
        val currentActivity = context as? Activity

        if (intentUriToProcess != null && currentActivity != null) {
            val actualCurrentIntentInActivity = currentActivity.intent
            // Ensure the activity's current intent data matches what our state reflects.
            if (actualCurrentIntentInActivity?.dataString == intentUriToProcess) {
                if (intentUriToProcess!!.contains("finplanner://")) {
                    Log.d(TAG_LOGIN_SCREEN, "Deep link URI '$intentUriToProcess' confirmed, calling handleIntent.")
                    handleIntent(actualCurrentIntentInActivity, tokenManager, navController, { isLoading = it }, context, scope)

                    // Clear the intent in the activity to prevent re-processing by MainActivity if it's not recreated.
                    actualCurrentIntentInActivity.data = null
                    Log.d(TAG_LOGIN_SCREEN, "Cleared data from activity's live intent.")

                    // Reset our state to null to signify handling is complete for this URI.
                    intentUriToProcess = null
                    Log.d(TAG_LOGIN_SCREEN, "Reset intentUriToProcess state to null post-handling.")
                } else {
                    Log.d(TAG_LOGIN_SCREEN, "URI '$intentUriToProcess' is not a 'finplanner' scheme. Resetting state.")
                    // If it's not a finplanner scheme but was processed by snapshotFlow, reset our state.
                    intentUriToProcess = null
                }
            } else {
                Log.d(TAG_LOGIN_SCREEN, "Stale URI? intentUriToProcess ('$intentUriToProcess') != activity's current intent data ('${actualCurrentIntentInActivity?.dataString}'). Ignoring this trigger.")
                // This case might indicate a race condition or rapid changes.
                // Resetting intentUriToProcess to null might be safest to await a fresh snapshotFlow emission.
                // However, if actualCurrentIntentInActivity.dataString is the *newer* one, snapshotFlow should emit it soon.
            }
        } else if (intentUriToProcess == null) {
            Log.d(TAG_LOGIN_SCREEN, "intentUriToProcess is null. Checking for initial login state...")
            // This block runs on initial composition (if no deep link) or after a deep link is handled and cleared.
            if (tokenManager.getToken() != null) {
                if (!isLoading) { // Avoid re-triggering if already verifying
                    Log.d(TAG_LOGIN_SCREEN, "Token found, verifying...")
                    verifyTokenAndNavigate(tokenManager, navController, { isLoading = it }, context, scope)
                }
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
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo and App Name Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(48.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Welcome to",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(
                                        fontWeight = FontWeight.ExtraBold, 
                                        fontSize = 28.sp, 
                                        color = MaterialTheme.colorScheme.primary
                                    )) {
                                        append("SolFin")
                                    }
                                },
                                textAlign = TextAlign.Start,
                                lineHeight = 32.sp
                            )
                        }
                    }

                    // Descriptive text
                    Text(
                        text = "Track your expenses, manage budgets, and achieve your financial goals with ease.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Sign in with Google Button
                    Button(
                        onClick = {
                            signInWithGoogle(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = "Google Icon",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Sign in with Google", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue Without Account Button
                    TextButton(
                        onClick = {
                            Log.d(TAG_LOGIN_SCREEN, "Continue Without Account button clicked")
                            tokenManager.setNoAccountMode(true)
                            navigateToDashboard(navController)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            "Continue Without Account", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview() {
    FinancialPlannerAppTheme {
        LoginScreen(navController = rememberNavController(), tokenManager = TokenManager(LocalContext.current))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    FinancialPlannerAppTheme(darkTheme = true) {
        LoginScreen(navController = rememberNavController(), tokenManager = TokenManager(LocalContext.current))
    }
}

// Helper functions (unchanged logic from your provided code)
private fun handleIntent(
    intent: Intent,
    tokenManager: TokenManager,
    navController: NavController,
    setIsLoading: (Boolean) -> Unit,
    context: Context,
    scope: CoroutineScope
) {
    val data = intent.data
    Log.d(TAG_LOGIN_SCREEN, "handleIntent: Processing intent data: $data")

    if (data != null && "finplanner" == data.scheme) {
        val token = data.getQueryParameter("token")
        val userId = data.getQueryParameter("userId")
        val email = data.getQueryParameter("email")
        val name = data.getQueryParameter("name")

        Log.d(TAG_LOGIN_SCREEN, "handleIntent: Received token via deep link: ${token?.substring(0, 20)}...")
        Log.d(TAG_LOGIN_SCREEN, "handleIntent: User data - ID: $userId, Email: $email, Name: $name")

        if (token != null && userId != null) {
            Log.d(TAG_LOGIN_SCREEN, "Valid deep link data found, processing login...")
            setIsLoading(true)

            tokenManager.saveToken(token)
            tokenManager.saveUserInfo(userId, email, name)
            tokenManager.setNoAccountMode(false)

            Log.d(TAG_LOGIN_SCREEN, "Token and user info saved successfully")
            Log.d(TAG_LOGIN_SCREEN, "Saved user - ID: $userId, Email: $email, Name: $name")

            setIsLoading(false)
            Toast.makeText(context, "Successfully logged in!", Toast.LENGTH_SHORT).show()

            intent.data = null

            navigateToDashboard(navController)

            Log.d(TAG_LOGIN_SCREEN, "Navigation to dashboard initiated")

            scope.launch {
                try {
                    val apiService = RetrofitClient.getApiService(context)
                    val authHeader = tokenManager.getAuthHeader()
                    if (authHeader != null) {
                        val response = apiService.getCurrentUser(authHeader)
                        if (response.isSuccessful) {
                            Log.d(TAG_LOGIN_SCREEN, "Backend token verification successful")
                        } else {
                            Log.w(TAG_LOGIN_SCREEN, "Backend token verification failed: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG_LOGIN_SCREEN, "Backend verification failed but user is already logged in: ${e.message}")
                }
            }
        } else {
            Toast.makeText(context, "Authentication failed: Invalid data received", Toast.LENGTH_SHORT).show()
            Log.e(TAG_LOGIN_SCREEN, "Deep link missing required data - token: $token, userId: $userId")
        }
    } else {
        Log.d(TAG_LOGIN_SCREEN, "handleIntent: No deep link data found")
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
            val apiService = RetrofitClient.getApiService(context)
            val authHeader = tokenManager.getAuthHeader()
            if (authHeader != null) {
                Log.d(TAG_LOGIN_SCREEN, "Verifying token with auth header: $authHeader")

                val response = apiService.getCurrentUser(authHeader)

                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        userResponse.user?.let { user ->
                            tokenManager.saveUserInfo(user.id, user.email, user.name)
                            Log.d(TAG_LOGIN_SCREEN, "Token verified successfully for user: ${user.name}")
                        }
                    }
                    navigateToDashboard(navController)
                } else {
                    Log.e(TAG_LOGIN_SCREEN, "Token verification failed: ${response.code()}")
                    if (response.code() == 401) {
                        Toast.makeText(context, "Your session has expired. Please login again.", Toast.LENGTH_LONG).show()
                        tokenManager.clearToken()
                    }
                }
            } else {
                Log.d(TAG_LOGIN_SCREEN, "No auth header available")
            }
        } catch (e: Exception) {
            Log.e(TAG_LOGIN_SCREEN, "Error verifying token: ${e.message}", e)
            Toast.makeText(context, "Could not verify login status. Please try again.", Toast.LENGTH_LONG).show()
        } finally {
            setIsLoading(false)
        }
    }
}

private fun navigateToDashboard(navController: NavController) {
    Log.d(TAG_LOGIN_SCREEN, "=== NAVIGATING TO DASHBOARD ===")
    try {
        navController.navigate("dashboard") {
            popUpTo("login") { inclusive = true }
            launchSingleTop = true
        }
        Log.d(TAG_LOGIN_SCREEN, "Dashboard navigation command executed successfully")
    } catch (e: Exception) {
        Log.e(TAG_LOGIN_SCREEN, "Error navigating to dashboard: ${e.message}", e)
    }
}

private fun signInWithGoogle(context: Context) {
    val googleAuthUrl = "${Config.BASE_URL}/api/auth/google"
    Log.d(TAG_LOGIN_SCREEN, "Attempting to sign in with Google: $googleAuthUrl")
    try {
        Toast.makeText(context, "Opening browser for Google Sign-In...", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleAuthUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open browser for Google Sign-In", Toast.LENGTH_LONG).show()
        Log.e(TAG_LOGIN_SCREEN, "Error starting Google Sign-In intent: ${e.message}", e)
    }
}
