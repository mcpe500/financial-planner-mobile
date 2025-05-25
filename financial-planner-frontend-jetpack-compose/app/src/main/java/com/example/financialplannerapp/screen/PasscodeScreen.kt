package com.example.financialplannerapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PasscodeScreen(navController: NavController) {
    var passcode by remember { mutableStateOf("") }
    val passcodeLength = 6

    fun handleSubmitPasscode(enteredPasscode: String) {
        // Replace with your actual passcode verification logic
        if (enteredPasscode == "123456") { // Example valid passcode
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true } // Or popUpTo("passcode") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            // Optionally, show an error message (e.g., Toast or a Text composable)
            passcode = "" // Reset passcode on failure
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text("Enter Passcode", fontSize = 24.sp)

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until passcodeLength) {
                PasscodeDot(isFilled = i < passcode.length)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val buttonModifier = Modifier
                .padding(4.dp)
                .size(72.dp)
            val numberButtonData = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9")
            )

            numberButtonData.forEach { rowData ->
                Row(horizontalArrangement = Arrangement.Center) {
                    rowData.forEach { number ->
                        Button(
                            onClick = {
                                if (passcode.length < passcodeLength) {
                                    passcode += number
                                    if (passcode.length == passcodeLength) {
                                        handleSubmitPasscode(passcode)
                                    }
                                }
                            },
                            modifier = buttonModifier
                        ) {
                            Text(number, fontSize = 20.sp)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = buttonModifier) // Placeholder for alignment
                Button(
                    onClick = {
                        if (passcode.length < passcodeLength) {
                            passcode += "0"
                            if (passcode.length == passcodeLength) {
                                handleSubmitPasscode(passcode)
                            }
                        }
                    },
                    modifier = buttonModifier
                ) {
                    Text("0", fontSize = 20.sp)
                }
                IconButton(
                    onClick = {
                        if (passcode.isNotEmpty()) {
                            passcode = passcode.dropLast(1)
                        }
                    },
                    modifier = buttonModifier
                ) {
                    Icon(Icons.Filled.Backspace, contentDescription = "Backspace")
                }
            }
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Login")
        }
    }
}

@Composable
fun PasscodeDot(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .padding(2.dp)
            .let {
                if (isFilled) it.background(Color.Black) else it.background(Color.LightGray)
            }
    )
}