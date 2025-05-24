package com.example.financialplannerapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasscodeScreen(
    onPasscodeEntered: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    var passcode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter Passcode",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = passcode,
            onValueChange = { 
                if (it.length <= 6) {
                    passcode = it
                    if (it.length == 6) {
                        onPasscodeEntered(it)
                    }
                }
            },
            label = { Text("6-digit Passcode") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onPasscodeEntered(passcode) },
            enabled = passcode.length == 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBackPressed) {
            Text("Back")
        }
    }
}