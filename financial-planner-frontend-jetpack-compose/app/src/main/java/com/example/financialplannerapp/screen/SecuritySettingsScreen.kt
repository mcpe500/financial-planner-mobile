package com.example.financialplannerapp.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private const val TAG_SECURITY = "SecuritySettingsScreen"

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val DarkGray = Color(0xFF424242)
private val MediumGray = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(navController: NavController) {
    Log.d(TAG_SECURITY, "SecuritySettingsScreen composing...")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Keamanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_SECURITY, "Back button clicked")
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BibitGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = "Keamanan",
                tint = BibitGreen,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Pengaturan Keamanan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Fitur ini sedang dalam pengembangan.\n\nAnda akan dapat:\n• Mengatur PIN aplikasi\n• Mengaktifkan autentikasi biometrik\n• Mengelola keamanan aplikasi",
                fontSize = 16.sp,
                color = MediumGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecuritySettingsScreenPreview() {
    SecuritySettingsScreen(navController = rememberNavController())
}