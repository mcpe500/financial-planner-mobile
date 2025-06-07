package com.example.financialplannerapp.data

import android.content.Context
import androidx.room.Room
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.local.dao.SecuritySettingsDao
import com.example.financialplannerapp.data.local.dao.UserProfileDao

/**
 * Database Manager - Simplified without Dagger/Hilt
 * 
 * Provides database instance and DAOs for the application.
 * Using singleton pattern instead of dependency injection to avoid KAPT issues.
 */
object DatabaseManager {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "financial-planner-db"
            ).build()
            INSTANCE = instance
            instance
        }
    }

    fun getUserProfileDao(context: Context): UserProfileDao {
        return getDatabase(context).userProfileDao()
    }

    fun getSecurityDao(context: Context): SecuritySettingsDao {
        return getDatabase(context).securitySettingsDao()
    }
}