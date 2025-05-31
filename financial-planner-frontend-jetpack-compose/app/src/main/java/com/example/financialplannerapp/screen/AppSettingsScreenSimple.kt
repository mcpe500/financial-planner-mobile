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
import com.example.financialplannerapp.service.ThemeService
import com.example.financialplannerapp.service.TranslationService

private const val TAG_APP_SETTINGS = "AppSettingsScreenSimple"

/**
 * Simple App Settings Screen
 * 
 * A working settings screen without complex state management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreenSimple(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val themeService = remember { ThemeService.getInstance() }
    val translationService = remember { TranslationService.getInstance() }
    
    // Simple state management
    var currentTheme by remember { mutableStateOf("system") }
    var currentLanguage by remember { mutableStateOf("id") }
    var currentCurrency by remember { mutableStateOf("IDR") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Theme Settings
        SimpleSettingsSection(
            title = "Theme Settings",
            icon = Icons.Default.Palette
        ) {
            ThemeSelectionCardSimple(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    currentTheme = theme
                    themeService.setTheme(theme)
                    Log.d(TAG_APP_SETTINGS, "Theme changed to: $theme")
                    Toast.makeText(context, "Theme changed to $theme", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Language Settings
        SimpleSettingsSection(
            title = "Language Settings",
            icon = Icons.Default.Language
        ) {
            LanguageSelectionCardSimple(
                currentLanguage = currentLanguage,
                onLanguageSelected = { language ->
                    currentLanguage = language
                    translationService.setLanguage(language)
                    Log.d(TAG_APP_SETTINGS, "Language changed to: $language")
                    Toast.makeText(context, "Language changed to $language", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SimpleSettingsSection(
    title: String,
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        content()
    }
}

@Composable
private fun ThemeSelectionCardSimple(
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
                "light" to "Light",
                "dark" to "Dark",
                "system" to "System"
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
private fun LanguageSelectionCardSimple(
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