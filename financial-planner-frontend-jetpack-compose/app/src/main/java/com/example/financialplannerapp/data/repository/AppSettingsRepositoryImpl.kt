package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.AppSettingsDao
import com.example.financialplannerapp.data.local.model.AppSettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    private val appSettingsDao: AppSettingsDao
) : AppSettingsRepository {

    override fun getAppSettings(): Flow<AppSettingsEntity?> {
        return appSettingsDao.getAppSettings()
    }

    override suspend fun getAppSettingsSync(): AppSettingsEntity? {
        return appSettingsDao.getAppSettingsSync()
    }

    override suspend fun insertAppSettings(appSettings: AppSettingsEntity): Long {
        return appSettingsDao.insertAppSettings(appSettings)
    }

    override suspend fun updateAppSettings(appSettings: AppSettingsEntity) {
        appSettingsDao.updateAppSettings(appSettings.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteAppSettings() {
        appSettingsDao.deleteAppSettings()
    }

    override suspend fun updateTheme(theme: String) {
        appSettingsDao.updateTheme(theme)
    }

    override suspend fun updateLanguage(language: String) {
        appSettingsDao.updateLanguage(language)
    }

    override suspend fun updateCurrency(currency: String) {
        appSettingsDao.updateCurrency(currency)
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        appSettingsDao.updateNotificationsEnabled(enabled)
    }
}
