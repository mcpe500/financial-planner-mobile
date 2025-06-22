package com.example.financialplannerapp.ui.screen.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.ui.viewmodel.DeleteAccountState
import com.example.financialplannerapp.ui.viewmodel.DeleteAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    
    val viewModel: DeleteAccountViewModel = viewModel {
        DeleteAccountViewModel(
            accountService = application.appContainer.accountService,
            tokenManager = application.appContainer.tokenManager
        )
    }
    
    val state by viewModel.state.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }
    
    // Handle state changes
    LaunchedEffect(state) {
        when (state) {
            is DeleteAccountState.OtpSent -> {
                showOtpDialog = true
                Toast.makeText(context, "Verification code sent to your email", Toast.LENGTH_SHORT).show()
            }
            is DeleteAccountState.AccountDeleted -> {
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is DeleteAccountState.Error -> {
                val message = (state as? DeleteAccountState.Error)?.message ?: "Unknown error"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Delete Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Warning Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Account Deletion Warning",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone. All your data will be permanently deleted.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "What will be deleted:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val deletionItems = listOf(
                        "• All your transactions and financial data",
                        "• Your profile and personal information",
                        "• All budgets and financial goals",
                        "• Receipt scans and transaction history",
                        "• App settings and preferences"
                    )
                    
                    deletionItems.forEach { item ->
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Delete Button
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                enabled = state !is DeleteAccountState.Loading
            ) {
                if (state is DeleteAccountState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text(
                        text = "Delete My Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Account Deletion",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you absolutely sure you want to delete your account? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.requestAccountDeletion()
                    }
                ) {
                    Text(
                        text = "Yes, Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // OTP Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Text(
                    text = "Enter Verification Code",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("We've sent a verification code to your email. Please enter it below:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("6-digit code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (state is DeleteAccountState.OtpVerified) {
                            viewModel.confirmDeletion()
                            showOtpDialog = false
                        } else {
                            viewModel.verifyOtp(otp)
                        }
                    },
                    enabled = otp.length == 6
                ) {
                    Text(
                        text = "Verify & Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}