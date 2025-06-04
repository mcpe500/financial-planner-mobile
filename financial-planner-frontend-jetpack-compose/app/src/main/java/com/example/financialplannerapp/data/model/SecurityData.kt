package com.example.financialplannerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * SecurityData - Data class for security settings information
 *
 * This represents security data that can be transferred between
 * different layers of the application (UI, repository, etc.)
 */
@Parcelize
data class SecurityData(
    val userId: String = "",
    val isPinEnabled: Boolean = false,
    val pinHash: String? = null,
    val isBiometricEnabled: Boolean = false,
    val isAutoLockEnabled: Boolean = false,
    val autoLockTimeout: Int = 5, // minutes
    val lastSecurityUpdate: Long = System.currentTimeMillis()
) : Parcelable

/**
 * Extension function to convert SecurityData to SecuritySettings entity
 */
fun SecurityData.toSecuritySettings(): SecuritySettings {
    return SecuritySettings(
        id = 1, // Fixed ID for app-wide settings
        pinHash = this.pinHash,
        isBiometricEnabled = this.isBiometricEnabled,
        isAutoLockEnabled = this.isAutoLockEnabled,
        autoLockTimeoutSeconds = this.autoLockTimeout * 60 // Convert minutes to seconds
    )
}

/**
 * Extension function to convert SecuritySettings entity to SecurityData
 */
fun SecuritySettings.toSecurityData(): SecurityData {
    return SecurityData(
        userId = "", // SecuritySettings doesn't have userId, use empty string
        isPinEnabled = this.pinHash != null,
        pinHash = this.pinHash,
        isBiometricEnabled = this.isBiometricEnabled,
        isAutoLockEnabled = this.isAutoLockEnabled,
        autoLockTimeout = this.autoLockTimeoutSeconds / 60, // Convert seconds to minutes
        lastSecurityUpdate = System.currentTimeMillis()
    )
}