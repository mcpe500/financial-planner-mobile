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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectAsState
import com.example.financialplannerapp.data.AppSettings
import com.example.financialplannerapp.service.SettingsManager
import com.example.financialplannerapp.service.ThemeService
import com.example.financialplannerapp.service.TranslationService
import com.example.financialplannerapp.utils.Translations

private const val TAG_APP_SETTINGS = "AppSettingsScreen"

/**
 * App Settings Screen
 * 
 * Complete settings management screen with working theme and language switching
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val themeService = remember { ThemeService.getInstance() }
    val translationService = remember { TranslationService.getInstance() }
    val settingsManager = remember { 
        SettingsManager.getInstance(context, themeService, translationService) 
    }
    val coroutineScope = rememberCoroutineScope()
    
    // State for current settings
    var currentTheme by remember { mutableStateOf("system") }
    var currentLanguage by remember { mutableStateOf("en") }
    var currentCurrency by remember { mutableStateOf("IDR") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    
    // Observe settings changes
    val settings by settingsManager.currentSettings.collectAsState(initial = AppSettings())
    
    // Update UI state when settings change
    LaunchedEffect(settings) {
        currentTheme = settings.theme
        currentLanguage = settings.language
        currentCurrency = settings.currency
        notificationsEnabled = settings.notificationsEnabled
        Log.d(TAG_APP_SETTINGS, "Settings updated: theme=${settings.theme}, language=${settings.language}")
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = Translations.get(Translations.Key.Back))
            }
            Text(
                text = Translations.get(Translations.Key.AppSettings),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Theme Settings
        SettingsSection(
            title = Translations.get(Translations.Key.ThemeSetting),
            description = Translations.get(Translations.Key.ThemeSettingDesc),
            icon = Icons.Default.Palette
        ) {
            ThemeSelectionCard(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    currentTheme = theme
                    coroutineScope.launch {
                        settingsManager.setTheme(theme)
                        Log.d(TAG_APP_SETTINGS, "Theme changed to: $theme")
                        Toast.makeText(context, Translations.get(Translations.Key.ThemeChangedTo, theme), Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Language Settings
        SettingsSection(
            title = Translations.get(Translations.Key.LanguageSetting),
            description = Translations.get(Translations.Key.LanguageSettingDesc),
            icon = Icons.Default.Language
        ) {
            LanguageSelectionCard(
                currentLanguage = currentLanguage,
                onLanguageSelected = { language ->
                    currentLanguage = language
                    coroutineScope.launch {
                        settingsManager.setLanguage(language)
                        Log.d(TAG_APP_SETTINGS, "Language changed to: $language")
                        Toast.makeText(context, "Language changed to $language", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Currency Settings
        SettingsSection(
            title = Translations.get(Translations.Key.CurrencySetting),
            description = Translations.get(Translations.Key.CurrencySettingDesc),
            icon = Icons.Default.AttachMoney
        ) {
            CurrencySelectionCard(
                currentCurrency = currentCurrency,
                onCurrencySelected = { currency ->
                    currentCurrency = currency
                    coroutineScope.launch {
                        settingsManager.setCurrency(currency)
                        Log.d(TAG_APP_SETTINGS, "Currency changed to: $currency")
                        Toast.makeText(context, Translations.get(Translations.Key.CurrencyChangedTo, currency), Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Notification Settings
        SettingsSection(
            title = Translations.get(Translations.Key.NotificationsSetting),
            description = Translations.get(Translations.Key.NotificationsSettingDesc),
            icon = Icons.Default.Notifications
        ) {
            NotificationSettingsCard(
                notificationsEnabled = notificationsEnabled,
                onNotificationsToggled = { enabled ->
                    notificationsEnabled = enabled
                    coroutineScope.launch {
                        settingsManager.setNotifications(enabled)
                        Log.d(TAG_APP_SETTINGS, "Notifications ${if (enabled) "enabled" else "disabled"}")
                        Toast.makeText(
                            context,
                            if (enabled) Translations.get(Translations.Key.NotificationsEnabled) 
                            else Translations.get(Translations.Key.NotificationsDisabled),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        content()
    }
}

@Composable
private fun ThemeSelectionCard(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val themeOptions = listOf(
                "light" to Translations.get(Translations.Key.ThemeLight),
                "dark" to Translations.get(Translations.Key.ThemeDark),
                "system" to Translations.get(Translations.Key.ThemeSystem)
            )
            
            themeOptions.forEach { (themeKey, themeName) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentTheme == themeKey,
                        onClick = { onThemeSelected(themeKey) }
                    )
                    Text(
                        text = themeName,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionCard(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val languageOptions = mapOf(
        "id" to "Bahasa Indonesia",
        "en" to "English",
        "zh" to "中文"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = languageOptions[currentLanguage] ?: "Unknown",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { (languageKey, languageName) ->
                    DropdownMenuItem(
                        text = { Text(languageName) },
                        onClick = {
                            onLanguageSelected(languageKey)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencySelectionCard(
    currentCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val currencyOptions = mapOf(
        "IDR" to Translations.get(Translations.Key.CurrencyIdr),
        "USD" to Translations.get(Translations.Key.CurrencyUsd),
        "EUR" to Translations.get(Translations.Key.CurrencyEur),
        "JPY" to Translations.get(Translations.Key.CurrencyJpy)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = currencyOptions[currentCurrency] ?: "Unknown",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencyOptions.forEach { (currencyKey, currencyName) ->
                    DropdownMenuItem(
                        text = { Text(currencyName) },
                        onClick = {
                            onCurrencySelected(currencyKey)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    notificationsEnabled: Boolean,
    onNotificationsToggled: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Translations.get(Translations.Key.EnableNotifications),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsToggled
            )
        }
    }
}