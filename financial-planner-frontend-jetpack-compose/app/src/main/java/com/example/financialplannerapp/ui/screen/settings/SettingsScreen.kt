package com.example.financialplannerapp.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.financialplannerapp.core.util.translate
import com.example.financialplannerapp.data.model.Translations

private const val TAG_SETTINGS_SCREEN = "SettingsScreen"

// Bibit-inspired color palette (consistent with DashboardScreen)
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)

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
            title = translate(Translations.PersonalProfile),
            subtitle = translate(Translations.Profile),
            icon = Icons.Filled.Person,
            route = "userProfileSettings"
        ),
        SettingItem(
            title = translate(Translations.Security),
            subtitle = translate(Translations.Security),
            icon = Icons.Filled.Security,
            route = "securitySettings"
        ),
        SettingItem(
            title = translate(Translations.AppSettings),
            subtitle = translate(Translations.AppInfo),
            icon = Icons.Filled.Settings,
            route = "appSettings"
        ),
        SettingItem(
            title = translate(Translations.DataSync),
            subtitle = translate(Translations.DataSyncDesc),
            icon = Icons.Filled.Sync,
            route = "dataSyncSettings",
            isOnlineRequired = true
        ),
        SettingItem(
            title = translate(Translations.BackupRestore),
            subtitle = translate(Translations.BackupRestoreDesc),
            icon = Icons.Filled.CloudUpload,
            route = "backupRestoreSettings"
        ),
        SettingItem(
            title = translate(Translations.HelpCenter),
            subtitle = translate(Translations.HelpCenterDesc),
            icon = Icons.Filled.HelpOutline,
            route = "helpCenterSettings"
        ),
        SettingItem(
            title = translate(Translations.ContactSupport),
            subtitle = translate(Translations.ContactSupportDesc),
            icon = Icons.Filled.ContactSupport,
            route = "contactSupportSettings",
            isOnlineRequired = true
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = translate(Translations.Settings),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray
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
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = translate(Translations.Back),
                            tint = BibitGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkGray,
                    navigationIconContentColor = BibitGreen
                )
            )
        },
        containerColor = SoftGray
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
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    tint = BibitGreen,
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
                    color = DarkGray
                )
                Text(
                    text = settingItem.subtitle,
                    fontSize = 14.sp,
                    color = MediumGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Online requirement indicator
                if (settingItem.isOnlineRequired) {
                    Text(
                        text = "Online Required",
                        fontSize = 12.sp,
                        color = BibitGreen,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Arrow Icon
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = MediumGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}