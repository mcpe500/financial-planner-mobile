package com.example.financialplannerapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financialplannerapp.TokenManager

// Data class for SecuritySettings if it doesn't exist elsewhere
data class SecuritySettings(
    val pinHash: String? = null,
    val isBiometricEnabled: Boolean = false,
    val isAutoLockEnabled: Boolean = true,
    val autoLockTimeoutSeconds: Int = 300
)

@Composable
fun SecuritySettingsScreen(
    navController: NavController,
    tokenManager: TokenManager? = null
) {
    // Use local state instead of Hilt ViewModel
    var securitySettings by remember {
        mutableStateOf(
            SecuritySettings(
                pinHash = null,
                isBiometricEnabled = false,
                isAutoLockEnabled = true,
                autoLockTimeoutSeconds = 300
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            error = null // Clear error after showing
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        var tempBiometricEnabled by remember { mutableStateOf(securitySettings.isBiometricEnabled) }
        var tempAutoLockEnabled by remember { mutableStateOf(securitySettings.isAutoLockEnabled) }
        var tempAutoLockTimeout by remember { mutableStateOf(securitySettings.autoLockTimeoutSeconds.toString()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Back button and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("← Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Security Settings", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PIN Management Section
            Text("Manage PIN", style = MaterialTheme.typography.titleMedium)
            if (securitySettings.pinHash != null) {
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { currentPin = it },
                    label = { Text("Current PIN (if changing/removing)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = newPin,
                onValueChange = { newPin = it },
                label = { Text(if (securitySettings.pinHash == null) "Set New PIN" else "New PIN") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            OutlinedTextField(
                value = confirmNewPin,
                onValueChange = { confirmNewPin = it },
                label = { Text("Confirm New PIN") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Button(onClick = {
                if (newPin.isNotEmpty() && newPin == confirmNewPin) {
                    // Update PIN hash (in real app, this should be properly hashed)
                    securitySettings = securitySettings.copy(pinHash = newPin.hashCode().toString())
                    currentPin = ""
                    newPin = ""
                    confirmNewPin = ""
                    Toast.makeText(context, "PIN Updated", Toast.LENGTH_SHORT).show()
                } else if (newPin.isEmpty() && securitySettings.pinHash != null && currentPin.isNotEmpty()) {
                    // Remove PIN
                    securitySettings = securitySettings.copy(pinHash = null)
                    Toast.makeText(context, "PIN Removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "PINs do not match or are empty.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(if (securitySettings.pinHash == null) "Set PIN" else "Update/Remove PIN")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Biometric Authentication
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Biometric Authentication")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = tempBiometricEnabled,
                    onCheckedChange = { 
                        tempBiometricEnabled = it 
                        securitySettings = securitySettings.copy(isBiometricEnabled = it)
                    }
                )
            }

            // Auto-lock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Auto-lock")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = tempAutoLockEnabled,
                    onCheckedChange = { 
                        tempAutoLockEnabled = it
                        securitySettings = securitySettings.copy(isAutoLockEnabled = it)
                    }
                )
            }

            if (tempAutoLockEnabled) {
                OutlinedTextField(
                    value = tempAutoLockTimeout,
                    onValueChange = { tempAutoLockTimeout = it },
                    label = { Text("Auto-lock timeout (seconds)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    val timeout = tempAutoLockTimeout.toIntOrNull() ?: securitySettings.autoLockTimeoutSeconds
                    securitySettings = securitySettings.copy(autoLockTimeoutSeconds = timeout)
                    Toast.makeText(context, "Auto-lock timeout updated", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Set Timeout")
                }
            }
        }
    }
}