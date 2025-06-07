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
import com.example.financialplannerapp.service.LocalReactiveSettingsService
import com.example.financialplannerapp.service.LocalAppContainer
import com.example.financialplannerapp.service.LocalTranslationProvider

/**
 * App Settings Screen with Reactive Settings Service
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onNavigateBack: () -> Unit = {}
) {    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Use services from AppProvider which are properly initialized
    val appContainer = LocalAppContainer.current
    val settingsService = LocalReactiveSettingsService.current
    val translationService = LocalTranslationProvider.current
    val dbHelper = appContainer.appSettingsDatabaseHelper
    
    // Collect current settings from reactive service
    val currentSettings by settingsService.currentSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = translationService.translate("app_settings")) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = translationService.translate("back")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)        ) {            // Get translations from translation service
            val themeLightText = translationService.translate("theme_light")
            val themeDarkText = translationService.translate("theme_dark")
            val themeSystemText = translationService.translate("theme_system")
            val themeSettingText = translationService.translate("theme_setting")
            val languageChangedText = translationService.translate("language_changed_to")
            
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
                },
                translationService = translationService
            )
                // Language Settings  
            LanguageSelectionCard(
                currentLanguage = currentSettings.language,
                onLanguageSelected = { language ->
                    settingsService.updateSettings(dbHelper) { settings ->
                        settings.copy(language = language)
                    }
                    
                    scope.launch {
                        val languageName = when (language) {
                            "id" -> "Bahasa Indonesia"
                            "en" -> "English"
                            "es" -> "Español"
                            else -> language
                        }
                        Toast.makeText(context, "$languageChangedText $languageName", Toast.LENGTH_SHORT).show()
                    }
                },
                translationService = translationService
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
                            "IDR" -> translationService.translate("currency_idr")
                            "USD" -> translationService.translate("currency_usd")
                            "EUR" -> translationService.translate("currency_eur")
                            "JPY" -> translationService.translate("currency_jpy")
                            else -> currency
                        }
                        val message = "${translationService.translate("currency_changed_to")} $currencyName"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                translationService = translationService
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
                            translationService.translate("notifications_enabled")
                        } else {
                            translationService.translate("notifications_disabled")
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                },
                translationService = translationService
            )
        }
    }
}

@Composable
private fun ThemeSelectionCard(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    translationService: com.example.financialplannerapp.data.model.TranslationProvider
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translationService.translate("theme_setting"),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translationService.translate("theme_setting_desc"),
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
                            "light" -> translationService.translate("theme_light")
                            "dark" -> translationService.translate("theme_dark")
                            "system" -> translationService.translate("theme_system")
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
    onLanguageSelected: (String) -> Unit,
    translationService: com.example.financialplannerapp.data.model.TranslationProvider
) {
    var expanded by remember { mutableStateOf(false) }
    
    val languages = listOf(
        "id" to "Bahasa Indonesia",
        "en" to "English", 
        "es" to "Español"
    )
    
    val currentLanguageName = languages.find { it.first == currentLanguage }?.second ?: "Select Language"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translationService.translate("language_setting"),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translationService.translate("language_setting_desc"),
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
    onCurrencySelected: (String) -> Unit,
    translationService: com.example.financialplannerapp.data.model.TranslationProvider
) {
    var expanded by remember { mutableStateOf(false) }
      val currencies = remember {
        listOf(
            "IDR" to "IDR",
            "USD" to "USD",
            "EUR" to "EUR",
            "JPY" to "JPY"
        )
    }
    
    // Get localized currency name based on current selection
    val getCurrencyDisplayName = { code: String ->
        when (code) {
            "IDR" -> "${translationService.translate("currency_idr")} (IDR)"
            "USD" -> "${translationService.translate("currency_usd")} (USD)"
            "EUR" -> "${translationService.translate("currency_eur")} (EUR)"
            "JPY" -> "${translationService.translate("currency_jpy")} (JPY)"
            else -> code
        }
    }
    
    val currentCurrencyName = getCurrencyDisplayName(currentCurrency)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = translationService.translate("currency_setting"),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = translationService.translate("currency_setting_desc"),
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
                    currencies.forEach { (code, _) ->
                        DropdownMenuItem(
                            text = { Text(getCurrencyDisplayName(code)) },
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
    onToggle: (Boolean) -> Unit,
    translationService: com.example.financialplannerapp.data.model.TranslationProvider
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
                    text = translationService.translate("notifications_setting"),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = translationService.translate("notifications_setting_desc"),
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
