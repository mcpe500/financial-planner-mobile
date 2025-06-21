package com.example.financialplannerapp.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector

// --- Wallet UI Data Class ---
// This is what your UI components will primarily work with.
// It directly uses Compose-friendly types like ImageVector and Color.
data class Wallet(
    val id: String,
    val name: String,
    val type: WalletType,
    val balance: Double,
    val icon: ImageVector, // Directly store ImageVector for UI
    val color: Color,      // Directly store Color for UI
    val isShared: Boolean = false,
    val memberCount: Int = 1
)

// --- WalletType Enum ---
enum class WalletType {
    CASH, BANK, E_WALLET, INVESTMENT, DEBT
}

// --- WalletType.icon Extension Property (for default icon based on type) ---
val WalletType.icon: ImageVector
    get() = when (this) {
        WalletType.CASH -> Icons.Default.Money
        WalletType.BANK -> Icons.Default.AccountBalance
        WalletType.E_WALLET -> Icons.Default.Smartphone
        WalletType.INVESTMENT -> Icons.Default.TrendingUp
        WalletType.DEBT -> Icons.Default.CreditCard
    }

// --- Helper Functions for String/Hex Conversion ---

/**
 * Converts a Compose Color object to a hexadecimal string (e.g., "#RRGGBB").
 */
fun Color.toHex(): String {
    return String.format("#%06X", (0xFFFFFF and this.toArgb()))
}

/**
 * Converts a hexadecimal color string (e.g., "#RRGGBB") to a Compose Color object.
 * Handles cases where the string might be invalid by returning a default color.
 */
fun String.toColor(): Color {
    return try {
        val hex = if (startsWith("#")) substring(1) else this
        Color(android.graphics.Color.parseColor("#$hex"))
    } catch (e: IllegalArgumentException) {
        Color.Black // Default to black if parsing fails
    }
}

/**
 * Maps a string icon name back to its corresponding ImageVector.
 * This is crucial for displaying icons retrieved as strings from the database.
 */
fun iconFromName(iconName: String?): ImageVector {
    return when (iconName) {
        Icons.Default.Wallet.name -> Icons.Default.Wallet
        Icons.Default.Money.name -> Icons.Default.Money
        Icons.Default.AccountBalance.name -> Icons.Default.AccountBalance
        Icons.Default.CreditCard.name -> Icons.Default.CreditCard
        Icons.Default.TrendingUp.name -> Icons.Default.TrendingUp
        Icons.Default.Savings.name -> Icons.Default.Savings
        Icons.Default.Home.name -> Icons.Default.Home
        Icons.Default.DirectionsCar.name -> Icons.Default.DirectionsCar
        Icons.Default.Fastfood.name -> Icons.Default.Fastfood
        Icons.Default.MedicalServices.name -> Icons.Default.MedicalServices
        Icons.Default.School.name -> Icons.Default.School
        Icons.Default.Smartphone.name -> Icons.Default.Smartphone
        Icons.Default.Group.name -> Icons.Default.Group
        else -> Icons.Default.Wallet // Fallback default icon
    }
}