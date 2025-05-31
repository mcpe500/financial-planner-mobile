package com.example.financialplannerapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.financialplannerapp.data.model.AppSettings

/**
 * App Settings DAO
 * 
 * Data Access Object for app settings operations.
 * Provides CRUD operations with reactive Flow support.
 */
@Dao
interface AppSettingsDao {
    
    /**
     * Get app settings as Flow for reactive updates
     */
    @Query("SELECT * FROM app_settings WHERE id = 0 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>
    
    /**
     * Get app settings once
     */
    @Query("SELECT * FROM app_settings WHERE id = 0 LIMIT 1")
    suspend fun getSettingsOnce(): AppSettings?
    
    /**
     * Insert app settings (replace if exists)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)
    
    /**
     * Update app settings
     */
    @Update
    suspend fun updateSettings(settings: AppSettings)
    
    /**
     * Delete all settings
     */
    @Query("DELETE FROM app_settings")
    suspend fun deleteAllSettings()
}