package com.example.financialplannerapp.data

import android.content.Context
import com.example.financialplannerapp.data.model.SecuritySettings
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for SecuritySettings operations using Room database
 * This replaces the old SQLiteOpenHelper approach
 */

@Singleton
class SecurityDatabaseHelper @Inject constructor(
    private val context: Context
) {

    private val database: AppDatabase by lazy {
        DatabaseManager.getDatabase(context)
    }

    private val securityDao by lazy {
        database.securityDao()
    }

    fun getSecuritySettings(): Flow<SecuritySettings?> {
        return securityDao.getSecuritySettings()
    }

    suspend fun updateSecuritySettings(settings: SecuritySettings) {
        securityDao.insertOrUpdateSecuritySettings(settings)
    }

    suspend fun updatePinHash(pinHash: String?) {
        securityDao.updatePinHash(pinHash)
    }

    suspend fun getPinHash(): String? {
        return securityDao.getPinHash()
    }

    suspend fun updateBiometricEnabled(enabled: Boolean) {
        securityDao.updateBiometricEnabled(enabled)
    }

    suspend fun updateAutoLockEnabled(enabled: Boolean) {
        securityDao.updateAutoLockEnabled(enabled)
    }

    suspend fun updateAutoLockTimeout(timeoutSeconds: Int) {
        securityDao.updateAutoLockTimeout(timeoutSeconds)
    }

    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray())
        return hashBytes.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    fun verifyPin(inputPin: String, storedHash: String): Boolean {
        return hashPin(inputPin) == storedHash
    }
}