package com.example.financialplannerapp.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "security_settings")
data class SecuritySettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Assuming a single row for app-wide settings

    @ColumnInfo(name = "pin_hash")
    var pinHash: String? = null,

    @ColumnInfo(name = "is_biometric_enabled")
    var isBiometricEnabled: Boolean = false,

    @ColumnInfo(name = "is_auto_lock_enabled")
    var isAutoLockEnabled: Boolean = false,

    @ColumnInfo(name = "auto_lock_timeout_seconds")
    var autoLockTimeoutSeconds: Int = 300 // Default to 5 minutes (300 seconds)
) : Parcelable