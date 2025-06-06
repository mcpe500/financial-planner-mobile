package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.SecuritySettingsDao
import com.example.financialplannerapp.data.local.model.SecuritySettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val securitySettingsDao: SecuritySettingsDao
) : SecurityRepository {

    override suspend fun getSecuritySettingsByUserId(userId: String): SecuritySettingsEntity? {
        return securitySettingsDao.getSecuritySettingsByUserId(userId)
    }

    override fun getSecuritySettingsByUserIdFlow(userId: String): Flow<SecuritySettingsEntity?> {
        return securitySettingsDao.getSecuritySettingsByUserIdFlow(userId)
    }

    override fun getAllSecuritySettings(): Flow<List<SecuritySettingsEntity>> {
        return securitySettingsDao.getAllSecuritySettings()
    }

    override suspend fun insertSecuritySettings(securitySettings: SecuritySettingsEntity): Long {
        return securitySettingsDao.insertSecuritySettings(securitySettings)
    }

    override suspend fun updateSecuritySettings(securitySettings: SecuritySettingsEntity) {
        securitySettingsDao.updateSecuritySettings(securitySettings.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteSecuritySettings(securitySettings: SecuritySettingsEntity) {
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
