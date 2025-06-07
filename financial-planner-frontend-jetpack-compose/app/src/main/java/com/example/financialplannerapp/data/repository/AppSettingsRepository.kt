package com.example.financialplannerapp.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.local.model.AppSettingsEntity

/**
 * Repository interface for App Settings
 */
interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettingsEntity?>
    suspend fun getSettingsOnce(): AppSettingsEntity?
    suspend fun saveSettings(settings: AppSettingsEntity)
    suspend fun updateTheme(theme: String)
    suspend fun updateLanguage(language: String)
    suspend fun updateCurrency(currency: String)
    suspend fun updateNotifications(enabled: Boolean)
    suspend fun resetSettings()
    suspend fun settingsExist(): Boolean
}