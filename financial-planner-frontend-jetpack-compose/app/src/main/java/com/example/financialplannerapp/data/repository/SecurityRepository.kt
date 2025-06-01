package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.dao.SecurityDao
import com.example.financialplannerapp.data.model.SecuritySettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface SecurityRepository {
    fun getSecuritySettings(): Flow<SecuritySettings?>
    suspend fun insertOrUpdateSecuritySettings(settings: SecuritySettings)
    suspend fun updatePinHash(pinHash: String?)
    suspend fun getPinHash(): String?
    suspend fun updateBiometricEnabled(isBiometricEnabled: Boolean)
    suspend fun updateAutoLockEnabled(isAutoLockEnabled: Boolean)
    suspend fun updateAutoLockTimeout(timeoutSeconds: Int)
}

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val securityDao: SecurityDao
) : SecurityRepository {

    override fun getSecuritySettings(): Flow<SecuritySettings?> = securityDao.getSecuritySettings()

    override suspend fun insertOrUpdateSecuritySettings(settings: SecuritySettings) {
        securityDao.insertOrUpdateSecuritySettings(settings)
    }

    override suspend fun updatePinHash(pinHash: String?) {
        securityDao.updatePinHash(pinHash)
    }

    override suspend fun getPinHash(): String? {
        return securityDao.getPinHash()
    }

    override suspend fun updateBiometricEnabled(isBiometricEnabled: Boolean) {
        securityDao.updateBiometricEnabled(isBiometricEnabled)
    }

    override suspend fun updateAutoLockEnabled(isAutoLockEnabled: Boolean) {
        securityDao.updateAutoLockEnabled(isAutoLockEnabled)
    }

    override suspend fun updateAutoLockTimeout(timeoutSeconds: Int) {
        securityDao.updateAutoLockTimeout(timeoutSeconds)
    }
}
