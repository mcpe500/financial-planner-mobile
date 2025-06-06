package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.SecuritySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecuritySettingsDao {
    
    @Query("SELECT * FROM security_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSecuritySettingsByUserId(userId: String): SecuritySettingsEntity?
    
    @Query("SELECT * FROM security_settings WHERE userId = :userId LIMIT 1")
    fun getSecuritySettingsByUserIdFlow(userId: String): Flow<SecuritySettingsEntity?>
    
    @Query("SELECT * FROM security_settings")
    fun getAllSecuritySettings(): Flow<List<SecuritySettingsEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecuritySettings(securitySettings: SecuritySettingsEntity): Long
    
    @Update
    suspend fun updateSecuritySettings(securitySettings: SecuritySettingsEntity)
    
    @Delete
    suspend fun deleteSecuritySettings(securitySettings: SecuritySettingsEntity)
    
    @Query("DELETE FROM security_settings WHERE userId = :userId")
    suspend fun deleteSecuritySettingsByUserId(userId: String)
    
    @Query("UPDATE security_settings SET isPinEnabled = :enabled WHERE userId = :userId")
    suspend fun updatePinEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security_settings SET isBiometricEnabled = :enabled WHERE userId = :userId")
    suspend fun updateBiometricEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security_settings SET sessionTimeoutMinutes = :timeout WHERE userId = :userId")
    suspend fun updateSessionTimeout(userId: String, timeout: Int)
    
    @Query("UPDATE security_settings SET isAutoLockEnabled = :enabled WHERE userId = :userId")
    suspend fun updateAutoLockEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security_settings SET failedLoginCount = :count, lastFailedLoginTimestamp = :timestamp WHERE userId = :userId")
    suspend fun updateFailedLoginAttempts(userId: String, count: Int, timestamp: Long)
    
    @Query("UPDATE security_settings SET isAccountLocked = :locked WHERE userId = :userId")
    suspend fun updateAccountLocked(userId: String, locked: Boolean)
    
    @Query("UPDATE security_settings SET failedLoginCount = 0, isAccountLocked = 0 WHERE userId = :userId")
    suspend fun resetFailedLoginAttempts(userId: String)
    
    @Query("DELETE FROM security_settings")
    suspend fun deleteAllSecuritySettings()
}
