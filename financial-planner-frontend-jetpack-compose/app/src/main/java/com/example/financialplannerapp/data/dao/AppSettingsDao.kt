package com.example.financialplannerapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.model.AppSettings

/**
 * Data Access Object for App Settings
 * 
 * Provides database operations for app settings including reactive Flow-based queries.
 * Uses REPLACE strategy to maintain single settings record.
 */
@Dao
interface AppSettingsDao {
    
    /**
     * Get app settings as Flow for reactive updates
     */
    @Query("SELECT * FROM app_settings WHERE id = 0 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>
    
    /**
     * Get app settings once (non-reactive)
     */
    @Query("SELECT * FROM app_settings WHERE id = 0 LIMIT 1")
    suspend fun getSettingsOnce(): AppSettings?
    
    /**
     * Insert or update settings
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)
    
    /**
     * Update existing settings
     */
    @Update
    suspend fun updateSettings(settings: AppSettings)
    
    /**
     * Delete all settings
     */
    @Query("DELETE FROM app_settings")
    suspend fun deleteAllSettings()
    
    /**
     * Check if settings exist
     */
    @Query("SELECT COUNT(*) FROM app_settings WHERE id = 0")
    suspend fun settingsExist(): Int
}