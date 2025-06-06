package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.SecurityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecuritySettingsDao {
    
    @Query("SELECT * FROM security WHERE userId = :userId LIMIT 1")
    suspend fun getSecuritySettingsByUserId(userId: String): SecurityEntity?
    
    @Query("SELECT * FROM security WHERE userId = :userId LIMIT 1")
    fun getSecuritySettingsByUserIdFlow(userId: String): Flow<SecurityEntity?>
    
    @Query("SELECT * FROM security")
    fun getAllSecuritySettings(): Flow<List<SecurityEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecuritySettings(securitySettings: SecurityEntity): Long
    
    @Update
    suspend fun updateSecuritySettings(securitySettings: SecurityEntity)
    
    @Delete
    suspend fun deleteSecuritySettings(securitySettings: SecurityEntity)
    
    @Query("DELETE FROM security WHERE userId = :userId")
    suspend fun deleteSecuritySettingsByUserId(userId: String)
    
    @Query("UPDATE security SET isPinEnabled = :enabled WHERE userId = :userId")
    suspend fun updatePinEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security SET isBiometricEnabled = :enabled WHERE userId = :userId")
    suspend fun updateBiometricEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security SET sessionTimeoutMinutes = :timeout WHERE userId = :userId")
    suspend fun updateSessionTimeout(userId: String, timeout: Int)
    
    @Query("UPDATE security SET isAutoLockEnabled = :enabled WHERE userId = :userId")
    suspend fun updateAutoLockEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE security SET sessionTimeoutMinutes = :timeout WHERE userId = :userId")
    suspend fun updateAutoLockTimeout(userId: String, timeout: Int)
    
    @Query("UPDATE security SET failedLoginCount = :count, lastFailedLoginTimestamp = :timestamp WHERE userId = :userId")
    suspend fun updateFailedLoginAttempts(userId: String, count: Int, timestamp: Long)
    
    @Query("UPDATE security SET isAccountLocked = :locked WHERE userId = :userId")
    suspend fun updateAccountLocked(userId: String, locked: Boolean)
    
    @Query("UPDATE security SET failedLoginCount = 0, isAccountLocked = 0 WHERE userId = :userId")
    suspend fun resetFailedLoginAttempts(userId: String)
    
    @Query("DELETE FROM security")
    suspend fun deleteAllSecuritySettings()
}
