package com.example.financialplannerapp.service

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.collectAsState

/**
 * Theme Service
 * 
 * Provides dynamic theme switching for the Financial Planner app.
 * Supports light, dark, and system themes with Bibit-inspired colors.
 * 
 * Features:
 * - Runtime theme switching
 * - System theme detection
 * - Reactive theme updates
 * - Material Design 3 color schemes
 * - Bibit-inspired green palette
 * 
 * Usage:
 * ```kotlin
 * val themeService = LocalThemeService.current
 * themeService.setTheme("dark")
 * ```
 */
open class ThemeService {
    
    companion object {
        @Volatile
        private var INSTANCE: ThemeService? = null
        
        fun getInstance(): ThemeService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeService().also { INSTANCE = it }
            }
        }
    }
    
    private val _currentTheme = MutableStateFlow("system")
    val currentTheme: Flow<String> = _currentTheme.asStateFlow()
    
    private var currentThemeMode: String = "system"
    
    /**
     * Set current theme
     * 
     * @param theme Theme mode ("light", "dark", "system")
     */
    open fun setTheme(theme: String) {
        if (theme in listOf("light", "dark", "system")) {
            currentThemeMode = theme
            _currentTheme.value = theme
        }
    }
    
    /**
     * Get current theme mode
     * 
     * @return Current theme mode
     */
    open fun getCurrentTheme(): String = currentThemeMode
    
    /**
     * Check if dark theme should be used
     * 
     * @param isSystemInDarkTheme Whether system is in dark theme
     * @return True if dark theme should be used
     */
    open fun isDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
        return when (currentThemeMode) {
            "light" -> false
            "dark" -> true
            "system" -> isSystemInDarkTheme
            else -> isSystemInDarkTheme
        }
    }
    
    /**
     * Get color scheme for current theme
     * 
     * @param isDark Whether dark theme is active
     * @return Material Design color scheme
     */
    open fun getColorScheme(isDark: Boolean): ColorScheme {
        return if (isDark) bibitDarkColorScheme else bibitLightColorScheme
    }
}

// Bibit-inspired color palette
private val BibitGreen = Color(0xFF4CAF50)
private val BibitLightGreen = Color(0xFF81C784)
private val BibitDarkGreen = Color(0xFF388E3C)
private val BibitAccent = Color(0xFF66BB6A)
private val SoftGray = Color(0xFFF5F5F5)
private val MediumGray = Color(0xFF9E9E9E)
private val DarkGray = Color(0xFF424242)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFF44336)

// Light theme color scheme
private val bibitLightColorScheme = lightColorScheme(
    primary = BibitGreen,
    onPrimary = Color.White,
    primaryContainer = BibitLightGreen,
    onPrimaryContainer = Color.White,
    secondary = BibitAccent,
    onSecondary = Color.White,
    secondaryContainer = BibitLightGreen,
    onSecondaryContainer = DarkGray,
    tertiary = WarningOrange,
    onTertiary = Color.White,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    background = Color.White,
    onBackground = DarkGray,
    surface = Color.White,
    onSurface = DarkGray,
    surfaceVariant = SoftGray,
    onSurfaceVariant = MediumGray,
    outline = MediumGray,
    outlineVariant = SoftGray,
    scrim = Color.Black,
    inverseSurface = DarkGray,
    inverseOnSurface = Color.White,
    inversePrimary = BibitLightGreen,
    surfaceDim = SoftGray,
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = SoftGray,
    surfaceContainer = SoftGray.copy(alpha = 0.5f),
    surfaceContainerHigh = SoftGray.copy(alpha = 0.8f),
    surfaceContainerHighest = SoftGray
)

// Dark theme color scheme
private val bibitDarkColorScheme = darkColorScheme(
    primary = BibitLightGreen,
    onPrimary = Color.Black,
    primaryContainer = BibitDarkGreen,
    onPrimaryContainer = Color.White,
    secondary = BibitAccent,
    onSecondary = Color.Black,
    secondaryContainer = BibitDarkGreen,
    onSecondaryContainer = Color.White,
    tertiary = WarningOrange,
    onTertiary = Color.Black,
    error = ErrorRed.copy(alpha = 0.8f),
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed.copy(alpha = 0.8f),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF6E6E6E),
    outlineVariant = Color(0xFF3E3E3E),
    scrim = Color.Black,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = BibitGreen,
    surfaceDim = Color(0xFF0E0E0E),
    surfaceBright = Color(0xFF2E2E2E),
    surfaceContainerLowest = Color(0xFF0A0A0A),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF2A2A2A),
    surfaceContainerHighest = Color(0xFF353535)
)

/**
 * Compose Local for Theme Service
 */
val LocalThemeService = compositionLocalOf<ThemeService> {
    error("No ThemeService provided")
}

/**
 * Theme Provider Composable
 * 
 * Provides theme service and applies Material Design theme.
 * 
 * @param theme Current theme mode
 * @param content App content
 */
@Composable
fun ThemeProvider(
    theme: String = "system",
    content: @Composable () -> Unit
) {
    val themeService = remember { ThemeService.getInstance() }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    
    // Update theme when it changes
    LaunchedEffect(theme) {
        themeService.setTheme(theme)
    }
    
    // Observe theme changes reactively
    val currentTheme by themeService.currentTheme.collectAsState(initial = theme)
    
    val isDarkTheme = themeService.isDarkTheme(isSystemInDarkTheme)
    val colorScheme = themeService.getColorScheme(isDarkTheme)
    
    CompositionLocalProvider(
        LocalThemeService provides themeService
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}