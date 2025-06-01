package com.example.financialplannerapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.financialplannerapp.ui.viewmodel.UserProfileViewModel
import com.example.financialplannerapp.data.model.UserProfileData

@Composable
fun UserProfileSettingsScreen(
    // Assuming userId is passed, e.g., from navigation or a logged-in user service
    // For a new profile, a special value like empty string could be used, 
    // or the ViewModel handles creation logic if no ID is passed.
    userId: String? = null, // Make userId nullable to handle new profile creation scenario
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    // Local states for text fields, initialized when userProfile is loaded or for new profile
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }
    var currencyPreference by remember { mutableStateOf("USD") }
    var notificationPreferences by remember { mutableStateOf(listOf("transactions", "alerts")) }

    // Effect to load profile if userId is provided and to update local states when profile changes
    LaunchedEffect(key1 = userId, key2 = userProfile) {
        if (userId != null && userId.isNotEmpty()) {
            viewModel.loadUserProfile(userId)
        }
        userProfile?.let {
            name = it.name ?: ""
            email = it.email ?: ""
            profilePictureUrl = it.profilePictureUrl ?: ""
            currencyPreference = it.currencyPreference ?: "USD"
            notificationPreferences = it.notificationPreferences
        }
    }
    
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError() // Clear error after showing
        }
    }

    if (isLoading && userProfile == null && userId != null) { // Show loading only if actively fetching an existing profile
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (userProfile == null && userId == null) "Create User Profile" 
                else "User Profile Settings", 
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = profilePictureUrl,
                onValueChange = { profilePictureUrl = it },
                label = { Text("Photo URL (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = currencyPreference,
                onValueChange = { currencyPreference = it },
                label = { Text("Currency Preference") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Enable Notifications")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = notificationPreferences.isNotEmpty(),
                    onCheckedChange = { enabled ->
                        notificationPreferences = if (enabled) {
                            listOf("transactions", "alerts")
                        } else {
                            emptyList()
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val currentProfile = userProfile
                    val profileToSave = UserProfileData(
                        userId = currentProfile?.userId ?: "", // Use existing userId or empty for new
                        name = name,
                        email = email,
                        profilePictureUrl = profilePictureUrl.ifEmpty { null },
                        currencyPreference = currencyPreference,
                        notificationPreferences = notificationPreferences,
                        lastLogin = System.currentTimeMillis(),
                        isDataModified = true,
                        phone = currentProfile?.phone,
                        dateOfBirth = currentProfile?.dateOfBirth,
                        occupation = currentProfile?.occupation,
                        monthlyIncome = currentProfile?.monthlyIncome,
                        financialGoals = currentProfile?.financialGoals ?: emptyList(),
                        lastSyncTime = currentProfile?.lastSyncTime ?: 0L,
                        createdAt = currentProfile?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    viewModel.saveUserProfile(profileToSave)
                    Toast.makeText(context, "Profile Saved", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (userProfile == null && userId == null) "Create Profile" else "Save Changes")
            }
            if (isLoading){
                 CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
    }
}