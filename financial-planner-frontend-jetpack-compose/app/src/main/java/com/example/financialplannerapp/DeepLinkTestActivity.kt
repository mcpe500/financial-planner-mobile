package com.example.financialplannerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DeepLinkTestActivity : ComponentActivity() {
    private val TAG = "DeepLinkTestActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "DeepLinkTestActivity created")
        Log.d(TAG, "Intent: ${intent}")
        Log.d(TAG, "Intent data: ${intent.data}")
        Log.d(TAG, "Intent action: ${intent.action}")
        
        setContent {
            DeepLinkTestScreen()
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "New intent received: ${intent.data}")
        setIntent(intent)
    }
}

@Composable
fun DeepLinkTestScreen() {
    val context = LocalContext.current as ComponentActivity
    val intent = context.intent
    val data = intent?.data
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Deep Link Test Screen",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (data != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Deep Link Data Received:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Full URI: ${data}")
                    Text("Scheme: ${data.scheme}")
                    Text("Host: ${data.host}")
                    Text("Path: ${data.path}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Query Parameters:", style = MaterialTheme.typography.titleSmall)
                    
                    data.getQueryParameter("token")?.let { token ->
                        Text("Token: ${token.take(20)}...")
                    }
                    data.getQueryParameter("userId")?.let { userId ->
                        Text("User ID: $userId")
                    }
                    data.getQueryParameter("email")?.let { email ->
                        Text("Email: $email")
                    }
                    data.getQueryParameter("name")?.let { name ->
                        Text("Name: $name")
                    }
                }
            }
        } else {
            Text("No deep link data found")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                // Test deep link
                val testUri = "finplanner://auth?token=test123&userId=testuser&email=test@example.com&name=Test%20User"
                val testIntent = Intent(Intent.ACTION_VIEW, Uri.parse(testUri))
                context.startActivity(testIntent)
            }
        ) {
            Text("Test Deep Link")
        }
        
        Button(
            onClick = {
                context.finish()
            }
        ) {
            Text("Close")
        }
    }
}