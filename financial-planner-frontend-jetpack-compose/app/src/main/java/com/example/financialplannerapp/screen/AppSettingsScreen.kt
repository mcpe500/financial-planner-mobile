package com.example.financialplannerapp.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper
import com.example.financialplannerapp.data.AppSettings
import com.example.financialplannerapp.service.LocalTranslator
import com.example.financialplannerapp.service.LocalThemeService
import com.example.financialplannerapp.service.TranslationProvider
import com.example.financialplannerapp.service.ThemeProvider
import com.example.financialplannerapp.service.AppProvider
import com.example.financialplannerapp.service.rememberSettingsManager

private const val TAG_APP_SETTINGS = "AppSettingsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(navController: NavController) {
    Log.d(TAG_APP_SETTINGS, "AppSettingsScreen composing...")
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val translator = LocalTranslator.current
    val themeService = LocalThemeService.current
    val settingsManager = rememberSettingsManager()
    
    // Database helper - using centralized helper with memory-safe context
    val databaseHelper = remember { AppSettingsDatabaseHelper.getInstance(context) }
    val userId = "default_user" // In real app, get from TokenManager
    
    // State for settings - load from database
    var appSettings by remember { mutableStateOf(AppSettings()) }
    
    // Load settings from database on startup
    LaunchedEffect(Unit) {
        appSettings = databaseHelper.getAppSettings(userId)
    }
    
    // Reactive settings updates
    LaunchedEffect(Unit) {
        settingsManager.getSettingsFlow().collect { settings ->
            appSettings = settings
        }
    }
    
    // Theme options with translations
    val themeOptions = mapOf(
        "light" to translator.get("theme_light"),
        "dark" to translator.get("theme_dark"),
        "system" to translator.get("theme_system")
    )
    
    // Language options with translations
    val languageOptions = mapOf(
        "id" to "Bahasa Indonesia",
        "en" to "English",
        "zh" to "中文"
    )
    
    // Currency options with translations
    val currencyOptions = mapOf(
        "IDR" to translator.get("currency_idr"),
        "USD" to translator.get("currency_usd"),
        "EUR" to translator.get("currency_eur"),
        "JPY" to translator.get("currency_jpy")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = translator.get("app_settings"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(TAG_APP_SETTINGS, "Back button clicked")
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = translator.get("back"),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Setting
            SettingCard(
                title = translator.get("theme_setting"),
                icon = Icons.Filled.Palette
            ) {
                Column {
                    Text(
                        text = translator.get("theme_setting_desc"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    themeOptions.forEach { (themeKey, themeName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = appSettings.theme == themeKey,
                                onClick = { 
                                    coroutineScope.launch {
                                        settingsManager.setTheme(themeKey)
                                        Log.d(TAG_APP_SETTINGS, "Theme changed to: $themeKey")
                                        Toast.makeText(
                                            context, 
                                            translator.get("theme_changed_to", themeName), 
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = themeName,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Language Setting
            SettingCard(
                title = translator.get("language_setting"),
                icon = Icons.Filled.Language
            ) {
                Column {
                    Text(
                        text = translator.get("language_setting_desc"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = languageOptions[appSettings.language] ?: "Bahasa Indonesia",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            languageOptions.forEach { (languageKey, languageName) ->
                                DropdownMenuItem(
                                    text = { Text(languageName) },
                                    onClick = {
                                        coroutineScope.launch {
                                            // Update language in settings manager and database
                                            settingsManager.setLanguage(languageKey)
                                            expanded = false
                                            Log.d(TAG_APP_SETTINGS, "Language changed to: $languageKey")
                                            
                                            // Note: Translation will update on next recomposition through AppProvider
                                            Toast.makeText(
                                                context, 
                                                "Language changed to $languageName", // Use static text for immediate feedback
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Currency Setting
            SettingCard(
                title = translator.get("currency_setting"),
                icon = Icons.Filled.AttachMoney
            ) {
                Column {
                    Text(
                        text = translator.get("currency_setting_desc"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = currencyOptions[appSettings.currency] ?: translator.get("currency_idr"),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            currencyOptions.forEach { (currencyKey, currencyName) ->
                                DropdownMenuItem(
                                    text = { Text(currencyName) },
                                    onClick = {
                                        coroutineScope.launch {
                                            settingsManager.setCurrency(currencyKey)
                                            expanded = false
                                            Log.d(TAG_APP_SETTINGS, "Currency changed to: $currencyKey")
                                            Toast.makeText(
                                                context, 
                                                translator.get("currency_changed_to", currencyName), 
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Notifications Setting
            SettingCard(
                title = translator.get("notifications_setting"),
                icon = Icons.Filled.Notifications
            ) {
                Column {
                    Text(
                        text = translator.get("notifications_setting_desc"),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = translator.get("enable_notifications"),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Switch(
                            checked = appSettings.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch {
                                    settingsManager.setNotifications(enabled)
                                    Log.d(TAG_APP_SETTINGS, "Notifications ${if (enabled) "enabled" else "disabled"}")
                                    Toast.makeText(
                                        context, 
                                        translator.get(if (enabled) "notifications_enabled" else "notifications_disabled"), 
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppSettingsScreenPreview() {
    AppProvider {
        AppSettingsScreen(navController = rememberNavController())
    }
}