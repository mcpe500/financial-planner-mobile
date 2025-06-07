package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.SecuritySettingsDao
import com.example.financialplannerapp.data.local.model.SecurityEntity
import kotlinx.coroutines.flow.Flow

class SecurityRepositoryImpl constructor(
    private val securitySettingsDao: SecuritySettingsDao
) : SecurityRepository {

    override suspend fun getSecuritySettingsByUserId(userId: String): SecurityEntity? {
        return securitySettingsDao.getSecuritySettingsByUserId(userId)
    }

    override fun getSecuritySettingsByUserIdFlow(userId: String): Flow<SecurityEntity?> {
        return securitySettingsDao.getSecuritySettingsByUserIdFlow(userId)
    }

    override fun getAllSecuritySettings(): Flow<List<SecurityEntity>> {
        return securitySettingsDao.getAllSecuritySettings()
    }

    override suspend fun insertSecuritySettings(securitySettings: SecurityEntity): Long {
        return securitySettingsDao.insertSecuritySettings(securitySettings)
    }

    override suspend fun updateSecuritySettings(securitySettings: SecurityEntity) {
        securitySettingsDao.updateSecuritySettings(securitySettings.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteSecuritySettings(securitySettings: SecurityEntity) {
        securitySettingsDao.deleteSecuritySettings(securitySettings)
    }

    override suspend fun deleteSecuritySettingsByUserId(userId: String) {
        securitySettingsDao.deleteSecuritySettingsByUserId(userId)
    }

    override suspend fun updatePinEnabled(userId: String, enabled: Boolean) {
        securitySettingsDao.updatePinEnabled(userId, enabled)
    }

    override suspend fun updateBiometricEnabled(userId: String, enabled: Boolean) {
        securitySettingsDao.updateBiometricEnabled(userId, enabled)
    }

    override suspend fun updateSessionTimeout(userId: String, timeout: Int) {
        securitySettingsDao.updateSessionTimeout(userId, timeout)
    }

    override suspend fun updateAutoLockEnabled(userId: String, enabled: Boolean) {
        securitySettingsDao.updateAutoLockEnabled(userId, enabled)
    }

    override suspend fun updateFailedLoginAttempts(userId: String, count: Int, timestamp: Long) {
        securitySettingsDao.updateFailedLoginAttempts(userId, count, timestamp)
    }

    override suspend fun updateAccountLocked(userId: String, locked: Boolean) {
        securitySettingsDao.updateAccountLocked(userId, locked)
    }

    override suspend fun resetFailedLoginAttempts(userId: String) {
        securitySettingsDao.resetFailedLoginAttempts(userId)
    }
}
