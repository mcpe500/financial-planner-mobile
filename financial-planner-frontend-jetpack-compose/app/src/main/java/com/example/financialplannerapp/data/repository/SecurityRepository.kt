package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.SecurityEntity
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    suspend fun getSecuritySettingsByUserId(userId: String): SecurityEntity?
    fun getSecuritySettingsByUserIdFlow(userId: String): Flow<SecurityEntity?>
    fun getAllSecuritySettings(): Flow<List<SecurityEntity>>
    suspend fun insertSecuritySettings(securitySettings: SecurityEntity): Long
    suspend fun updateSecuritySettings(securitySettings: SecurityEntity)
    suspend fun deleteSecuritySettings(securitySettings: SecurityEntity)
    suspend fun deleteSecuritySettingsByUserId(userId: String)
    suspend fun updatePinEnabled(userId: String, enabled: Boolean)
    suspend fun updateBiometricEnabled(userId: String, enabled: Boolean)
    suspend fun updateSessionTimeout(userId: String, timeout: Int)
    suspend fun updateAutoLockEnabled(userId: String, enabled: Boolean)
    suspend fun updateFailedLoginAttempts(userId: String, count: Int, timestamp: Long)
    suspend fun updateAccountLocked(userId: String, locked: Boolean)
    suspend fun resetFailedLoginAttempts(userId: String)
}
