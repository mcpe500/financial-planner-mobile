package com.example.financialplannerapp.data.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * SecurityEntity - Entity class for security settings in Room database
 */
@Parcelize
@Entity(tableName = "security")
data class SecurityEntity(
    @PrimaryKey
    val userId: String = "",
    val isPinEnabled: Boolean = false,
    val pinHash: String? = null,
    val isBiometricEnabled: Boolean = false,
    val sessionTimeoutMinutes: Int = 15,
    val isAutoLockEnabled: Boolean = true,
    val maxLoginAttempts: Int = 3,
    val lockoutDurationMinutes: Int = 30,
    val lastFailedLoginTimestamp: Long = 0L,
    val failedLoginCount: Int = 0,
    val isAccountLocked: Boolean = false,
    val securityQuestionHash: String? = null,
    val securityAnswerHash: String? = null,
    val twoFactorEnabled: Boolean = false,
    val backupEmail: String? = null,
    val lastPasswordChangeTimestamp: Long = 0L,
    val passwordExpiryDays: Int = 90,
    val isDataEncryptionEnabled: Boolean = true,
    val encryptionLevel: String = "AES256",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable
