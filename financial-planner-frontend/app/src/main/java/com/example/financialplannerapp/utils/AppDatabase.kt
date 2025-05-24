package com.example.financialplannerapp.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.financialplannerapp.db.UserProfileDao
import com.example.financialplannerapp.db.HelpContentDao
import com.example.financialplannerapp.models.roomdb.UserProfile
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.models.roomdb.FAQItem

@Database(entities = [UserProfile::class, HelpContent::class, FAQItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun helpContentDao(): HelpContentDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_planner_database"
                )
                .fallbackToDestructiveMigration() // For development - recreates DB on schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}