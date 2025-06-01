package com.example.financialplannerapp.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * Theme Provider for the Financial Planner App
 * 
 * Manages app theme based on user preferences and system settings.
 * Supports light, dark, and system automatic themes.
 */

// Define color schemes for the app
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF018786),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF6200EE),
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFA7F3FF),
    tertiary = Color(0xFFBB86FC),
    onTertiary = Color.Black,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

/**
 * Theme Provider Composable
 * 
 * Provides theme context based on user preference:
 * - "light": Force light theme
 * - "dark": Force dark theme  
 * - "system": Follow system theme
 */
@Composable
fun ThemeProvider(
    theme: String = "system",
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val darkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        "system" -> systemInDarkTheme
        else -> systemInDarkTheme // Default to system
    }
    
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

/**
 * Current Theme Provider
 * Provides reactive theme state throughout the app
 */
class ThemeState {
    private val _currentTheme = mutableStateOf("system")
    val currentTheme: State<String> = _currentTheme
    
    fun setTheme(theme: String) {
        _currentTheme.value = theme
    }
    
    companion object {
        @Volatile
        private var INSTANCE: ThemeState? = null
        
        fun getInstance(): ThemeState {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeState().also { INSTANCE = it }
            }
        }
    }
}

/**
 * Theme Hook - Use this to get current theme in Composables
 */
@Composable
fun rememberThemeState(): ThemeState {
    return remember { ThemeState.getInstance() }
}

/**
 * Check if current theme is dark
 */
@Composable
fun isDarkTheme(theme: String): Boolean {
    val systemInDarkTheme = isSystemInDarkTheme()
    return when (theme) {
        "light" -> false
        "dark" -> true
        "system" -> systemInDarkTheme
        else -> systemInDarkTheme
    }
}