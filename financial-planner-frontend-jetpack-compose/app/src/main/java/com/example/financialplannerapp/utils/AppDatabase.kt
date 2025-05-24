package com.example.financialplannerapp.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financialplannerapp.db.Converters
import com.example.financialplannerapp.db.HelpContentDao
import com.example.financialplannerapp.models.dao.UserProfileDao
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.models.roomdb.UserProfile

@Database(entities = [UserProfile::class, FAQItem::class, HelpContent::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
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
                .fallbackToDestructiveMigration() // Add this for now, define migrations later
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}