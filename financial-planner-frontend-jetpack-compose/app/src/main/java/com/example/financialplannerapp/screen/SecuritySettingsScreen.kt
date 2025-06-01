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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financialplannerapp.ui.viewmodel.SecurityViewModel
import com.example.financialplannerapp.data.model.SecuritySettings

@Composable
fun SecuritySettingsScreen(
    viewModel: SecurityViewModel = hiltViewModel(),
    navController: NavController
) {
    val securitySettings by viewModel.securitySettings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError() // Clear error after showing
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        securitySettings?.let { settings ->
            var tempBiometricEnabled by remember { mutableStateOf(settings.isBiometricEnabled) }
            var tempAutoLockEnabled by remember { mutableStateOf(settings.isAutoLockEnabled) }
            var tempAutoLockTimeout by remember { mutableStateOf(settings.autoLockTimeoutSeconds.toString()) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Security Settings", style = MaterialTheme.typography.headlineSmall)

                // PIN Management Section
                Text("Manage PIN", style = MaterialTheme.typography.titleMedium)
                if (settings.pinHash != null) {
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
                    label = { Text(if (settings.pinHash == null) "Set New PIN" else "New PIN") },
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
                        // Basic validation, actual hashing and comparison should be more robust
                        // For simplicity, directly updating. In real app, hash newPin before saving.
                        viewModel.updatePinHash(newPin, settings) // Pass newPin to be hashed by ViewModel/Repo
                        currentPin = ""
                        newPin = ""
                        confirmNewPin = ""
                        Toast.makeText(context, "PIN Updated", Toast.LENGTH_SHORT).show()
                    } else if (newPin.isEmpty() && settings.pinHash != null && currentPin.isNotEmpty()) {
                         // Logic to verify currentPin before removing would go here
                        viewModel.updatePinHash(null, settings) // Remove PIN
                        Toast.makeText(context, "PIN Removed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "PINs do not match or are empty.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(if (settings.pinHash == null) "Set PIN" else "Update/Remove PIN")
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
                            viewModel.updateSecuritySettings(settings.copy(isBiometricEnabled = it))
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
                            viewModel.updateSecuritySettings(settings.copy(isAutoLockEnabled = it))
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
                        val timeout = tempAutoLockTimeout.toIntOrNull() ?: settings.autoLockTimeoutSeconds
                        viewModel.updateSecuritySettings(settings.copy(autoLockTimeoutSeconds = timeout))
                        Toast.makeText(context, "Auto-lock timeout updated", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Set Timeout")
                    }
                }
            }
        } ?: run {
            Text("Loading security settings...") // Show if settings are null initially
        }
    }
}