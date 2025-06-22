package com.example.financialplannerapp.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private const val TAG_SETTINGS_SCREEN = "SettingsScreen"

data class SettingItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val isOnlineRequired: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Log.d(TAG_SETTINGS_SCREEN, "SettingsScreen composing...")

    val settingCategories = listOf(
        SettingItem(
            title = "Personal Profile",
            subtitle = "Manage your personal information",
            icon = Icons.Filled.Person,
            route = "userProfileSettings"
        ),
        SettingItem(
            title = "Security",
            subtitle = "Password, PIN, and security settings",
            icon = Icons.Filled.Security,
            route = "securitySettings"
        ),
        SettingItem(
            title = "App Settings",
            subtitle = "Theme, language, and app preferences",
            icon = Icons.Filled.Settings,
            route = "appSettings"
        ),
        SettingItem(
            title = "Data Sync",
            subtitle = "Sync data across devices",
            icon = Icons.Filled.Sync,
            route = "dataSyncSettings",
            isOnlineRequired = true
        ),
        SettingItem(
            title = "Backup & Restore",
            subtitle = "Backup and restore your data",
            icon = Icons.Filled.CloudUpload,
            route = "backupRestoreSettings"
        ),
        SettingItem(
            title = "Help Center",
            subtitle = "FAQs and user guides",
            icon = Icons.Filled.HelpOutline,
            route = "helpCenterSettings"
        ),
        SettingItem(
            title = "Contact Support",
            subtitle = "Get help from our support team",
            icon = Icons.Filled.ContactSupport,
            route = "contactSupportSettings",
            isOnlineRequired = true
        ),
        SettingItem(
            title = "Delete Account",
            subtitle = "Permanently delete your account and data",
            icon = Icons.Filled.Delete,
            route = "deleteAccount",
            isOnlineRequired = true
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_SETTINGS_SCREEN, "Back button clicked")
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(settingCategories) { settingItem ->
                SettingCard(
                    settingItem = settingItem,
                    onClick = {
                        Log.d(TAG_SETTINGS_SCREEN, "Navigating to ${settingItem.route}")
                        navController.navigate(settingItem.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingCard(
    settingItem: SettingItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = settingItem.icon,
                    contentDescription = settingItem.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = settingItem.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = settingItem.subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Online requirement indicator
                if (settingItem.isOnlineRequired) {
                    Text(
                        text = "Online Required",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Arrow Icon
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(navController = rememberNavController())
    }
}