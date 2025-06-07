package com.example.financialplannerapp.ui.screen.settings

import android.widget.Toast
import com.example.financialplannerapp.service.ReactiveSettingsService
import com.example.financialplannerapp.data.AppSettings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.example.financialplannerapp.core.util.Translations
import com.example.financialplannerapp.core.util.translate
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper
import com.example.financialplannerapp.service.LocalSettingsService

/**
 * App Settings Screen with Reactive Settings Service
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dbHelper = remember { AppSettingsDatabaseHelper.getInstance(context) }
    val settingsService = ReactiveSettingsService.getInstance()
    
    // Collect current settings from reactive service
    val currentSettings by settingsService.currentSettings.collectAsState(initial = AppSettings())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = translate(Translations.Key.AppSettings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = translate(Translations.Key.Back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)        ) {
            // Get translations at composable level
            val themeLightText = translate(Translations.Key.ThemeLight)
            val themeDarkText = translate(Translations.Key.ThemeDark)
            val themeSystemText = translate(Translations.Key.ThemeSystem)
            val themeSettingText = translate(Translations.Key.ThemeSetting)
            val languageChangedText = translate(Translations.Key.LanguageChangedTo)
            
            // Theme Settings
            ThemeSelectionCard(
                currentTheme = currentSettings.theme,
                onThemeSelected = { theme ->
                    settingsService.updateSettings(dbHelper) { settings ->
                        settings.copy(theme = theme)
                    }
                    
                    scope.launch {
                        val themeName = when (theme) {
                            "light" -> themeLightText
                            "dark" -> themeDarkText
                            "system" -> themeSystemText
                            else -> theme
                        }
                        val message = "$themeSettingText changed to $themeName"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
              
            // Language Settings  
            LanguageSelectionCard(
                currentLanguage = currentSettings.language,
                onLanguageSelected = { language ->
                    settingsService.updateSettings(dbHelper) { settings ->
                        settings.copy(language = language)
                    }
                    
                    scope.launch {
                        kotlinx.coroutines.delay(100)
                        val languageName = when (language) {
                            "id" -> "Bahasa Indonesia"
                            "en" -> "English"
                            "zh" -> "中文"
                            else -> language
                        }
                        Toast.makeText(context, "$languageChangedText $languageName", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            // Currency Settings
            CurrencySelectionCard(
                currentCurrency = currentSettings.currency,
                onCurrencySelected = { currency ->
                    settingsService.updateSettings(dbHelper) { settings ->
                        settings.copy(currency = currency)
                    }
                    
                    scope.launch {
                        val currencyName = when (currency) {
                            "IDR" -> "Indonesian Rupiah"
                            "USD" -> "US Dollar"
                            "EUR" -> "Euro"
                            "JPY" -> "Japanese Yen"
                            else -> currency
                        }
                        val message = "Currency changed to $currencyName"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            // Notification Settings
            NotificationSettingsCard(
                enabled = currentSettings.notificationsEnabled,
                onToggle = { enabled ->
                    settingsService.updateSettings(dbHelper) { settings ->
                        settings.copy(notificationsEnabled = enabled)
                    }
                    
                    scope.launch {
                        val message = if (enabled) {
                            "Notifications enabled"
                        } else {
                            "Notifications disabled"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
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
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translate(Translations.Key.ThemeSetting),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translate(Translations.Key.ThemeSettingDesc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            val themes = remember {
                listOf(
                    "light" to "Light",
                    "dark" to "Dark", 
                    "system" to "System"
                )
            }
            
            themes.forEach { (themeCode, themeName) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = currentTheme == themeCode,
                        onClick = { onThemeSelected(themeCode) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (themeCode) {
                            "light" -> translate(Translations.Key.ThemeLight)
                            "dark" -> translate(Translations.Key.ThemeDark)
                            "system" -> translate(Translations.Key.ThemeSystem)
                            else -> themeName
                        },
                        style = MaterialTheme.typography.bodyMedium
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
    
    val languages = listOf(
        "id" to "Bahasa Indonesia",
        "en" to "English", 
        "zh" to "中文"
    )
    
    val currentLanguageName = languages.find { it.first == currentLanguage }?.second ?: "Select Language"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translate(Translations.Key.LanguageSetting),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translate(Translations.Key.LanguageSettingDesc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currentLanguageName,
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
                    languages.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                onLanguageSelected(code)
                                expanded = false
                            },
                            leadingIcon = if (currentLanguage == code) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
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
    
    val currencies = remember {
        listOf(
            "IDR" to "Indonesian Rupiah (IDR)",
            "USD" to "US Dollar (USD)",
            "EUR" to "Euro (EUR)",
            "JPY" to "Japanese Yen (JPY)"
        )
    }
    
    val currentCurrencyName = currencies.find { it.first == currentCurrency }?.second ?: "Select Currency"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translate(Translations.Key.CurrencySetting),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translate(Translations.Key.CurrencySettingDesc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = currentCurrencyName,
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
                    currencies.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                onCurrencySelected(code)
                                expanded = false
                            },
                            leadingIcon = if (currentCurrency == code) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = translate(Translations.Key.NotificationsSetting),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = translate(Translations.Key.NotificationsSettingDesc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}
