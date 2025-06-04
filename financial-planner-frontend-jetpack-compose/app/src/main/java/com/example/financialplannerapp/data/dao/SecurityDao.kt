package com.example.financialplannerapp.data.dao

import androidx.room.*
import com.example.financialplannerapp.data.model.SecuritySettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSecuritySettings(settings: SecuritySettings)

    // Get all security settings (typically one row)
    // Using a fixed ID (e.g., 1) as defined in the SecuritySettings entity's PrimaryKey
    @Query("SELECT * FROM security_settings WHERE id = 1 LIMIT 1")
    fun getSecuritySettings(): Flow<SecuritySettings?>

    // Specific updates - consider if updating the whole object is simpler via insertOrUpdateSecuritySettings

    @Query("UPDATE security_settings SET pin_hash = :pinHash WHERE id = 1")
    suspend fun updatePinHash(pinHash: String?)

    @Query("SELECT pin_hash FROM security_settings WHERE id = 1 LIMIT 1")
    suspend fun getPinHash(): String?

    @Query("UPDATE security_settings SET is_biometric_enabled = :isBiometricEnabled WHERE id = 1")
    suspend fun updateBiometricEnabled(isBiometricEnabled: Boolean)

    @Query("UPDATE security_settings SET is_auto_lock_enabled = :isAutoLockEnabled WHERE id = 1")
    suspend fun updateAutoLockEnabled(isAutoLockEnabled: Boolean)

    @Query("UPDATE security_settings SET auto_lock_timeout_seconds = :timeoutSeconds WHERE id = 1")
    suspend fun updateAutoLockTimeout(timeoutSeconds: Int)
}