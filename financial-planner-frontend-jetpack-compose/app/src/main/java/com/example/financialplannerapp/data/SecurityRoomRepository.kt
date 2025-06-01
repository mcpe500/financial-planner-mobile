package com.example.financialplannerapp.data

import com.example.financialplannerapp.data.dao.SecurityDao
import com.example.financialplannerapp.data.model.SecuritySettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SecurityRoomRepository @Inject constructor(private val securityDao: SecurityDao) {

    fun getSecuritySettings(): Flow<SecuritySettings?> { // Removed id parameter
        return securityDao.getSecuritySettings()
    }

    suspend fun insertOrUpdateSecuritySettings(settings: SecuritySettings) {
        securityDao.insertOrUpdateSecuritySettings(settings)
    }

    suspend fun updatePinHash(pinHash: String?) { // Removed id parameter, changed to nullable String
        securityDao.updatePinHash(pinHash)
    }

    suspend fun getPinHash(): String? { // Removed id parameter
        return securityDao.getPinHash()
    }

    suspend fun verifyPin(pinHash: String): Boolean { // Removed id parameter
        val storedHash = securityDao.getPinHash()
        return storedHash == pinHash
    }

    suspend fun updateBiometricEnabled(biometricEnabled: Boolean) { // Removed id parameter
        securityDao.updateBiometricEnabled(biometricEnabled)
    }

    suspend fun updateAutoLockEnabled(autoLockEnabled: Boolean) { // Removed id parameter
        securityDao.updateAutoLockEnabled(autoLockEnabled)
    }

    suspend fun updateAutoLockTimeout(timeoutSeconds: Int) { // Removed id parameter, changed parameter name
        securityDao.updateAutoLockTimeout(timeoutSeconds)
    }
}