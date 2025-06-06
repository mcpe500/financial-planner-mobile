package com.example.financialplannerapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financialplannerapp.data.local.dao.AppSettingsDao
import com.example.financialplannerapp.data.local.dao.SecurityDao
import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.model.AppSettingsEntity
import com.example.financialplannerapp.data.local.model.SecuritySettingsEntity
import com.example.financialplannerapp.data.local.model.UserProfileEntity

@Database(
    entities = [
        AppSettingsEntity::class,
        UserProfileEntity::class,
        SecuritySettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun securityDao(): SecurityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_planner_db"
                )
                .fallbackToDestructiveMigration() // Consider a proper migration strategy for production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}