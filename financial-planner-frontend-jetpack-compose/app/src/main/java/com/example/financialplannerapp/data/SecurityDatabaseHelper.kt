package com.example.financialplannerapp.data

import android.content.Context
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.local.model.SecurityEntity
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
        database.securitySettingsDao()
    }

    fun getSecuritySettings(): Flow<SecurityEntity?> {
        return securityDao.getSecuritySettingsByUserIdFlow("default_user")
    }

    suspend fun updateSecuritySettings(settings: SecurityEntity) {
        securityDao.insertSecuritySettings(settings)
    }

    suspend fun updatePinEnabled(enabled: Boolean) {
        securityDao.updatePinEnabled("default_user", enabled)
    }

    suspend fun updateBiometricEnabled(enabled: Boolean) {
        securityDao.updateBiometricEnabled("default_user", enabled)
    }

    suspend fun updateAutoLockEnabled(enabled: Boolean) {
        securityDao.updateAutoLockEnabled("default_user", enabled)
    }

    suspend fun updateSessionTimeout(timeout: Int) {
        securityDao.updateSessionTimeout("default_user", timeout)
    }

    suspend fun updateAutoLockTimeout(timeoutSeconds: Int) {
        securityDao.updateAutoLockTimeout("default_user", timeoutSeconds)
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