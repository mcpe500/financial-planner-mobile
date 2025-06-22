package com.example.financialplannerapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Import colors from Color.kt
import com.example.financialplannerapp.ui.theme.BibitGreen
import com.example.financialplannerapp.ui.theme.BibitLightGreen
import com.example.financialplannerapp.ui.theme.SoftGray
import com.example.financialplannerapp.ui.theme.MediumGray
import com.example.financialplannerapp.ui.theme.DarkGray
import com.example.financialplannerapp.ui.theme.WarningOrange
import com.example.financialplannerapp.ui.theme.DangerRed

// Additional colors needed for theme
private val BibitDarkGreen = Color(0xFF388E3C)
private val BibitAccent = Color(0xFF66BB6A)
private val ErrorRed = DangerRed

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
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
private val DarkColorScheme = darkColorScheme(
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

@Composable
fun FinancialPlannerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to use our custom color scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}