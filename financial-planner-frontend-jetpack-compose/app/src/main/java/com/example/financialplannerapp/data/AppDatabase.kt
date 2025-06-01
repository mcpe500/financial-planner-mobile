package com.example.financialplannerapp.data

import androidx.room.*
import androidx.room.Database
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.financialplannerapp.data.dao.AppSettingsDao
import com.example.financialplannerapp.data.model.AppSettings

/**
 * Room Database for Financial Planner App
 * 
 * Main database class that provides access to DAOs and manages database creation.
 * Includes app settings table for storing user preferences.
 */
@Database(
    entities = [AppSettings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to app settings DAO
     */
    abstract fun appSettingsDao(): AppSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get database instance with singleton pattern
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_planner_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Database created, you can add initial data here if needed
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Close database (for testing purposes)
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

/**
 * Type converters for Room database
 */
class DatabaseConverters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
}