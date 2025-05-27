package com.example.financialplannerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.ui.theme.FinancialPlannerAppTheme

private const val TAG_MAIN_ACTIVITY = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG_MAIN_ACTIVITY, "MainActivity onCreate")
        Log.d(TAG_MAIN_ACTIVITY, "Intent: ${intent?.dataString}")
        
        tokenManager = TokenManager(this)

        setContent {
            FinancialPlannerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        tokenManager = tokenManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG_MAIN_ACTIVITY, "onNewIntent called with: ${intent.dataString}")
        setIntent(intent)
    }
}