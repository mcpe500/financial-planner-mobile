package com.example.financialplannerapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.dao.AppSettingsDao
import com.example.financialplannerapp.data.local.dao.CategoryDao
import com.example.financialplannerapp.data.local.dao.ReceiptTransactionDao
import com.example.financialplannerapp.data.local.dao.SecuritySettingsDao
import com.example.financialplannerapp.data.local.dao.TransactionDao
import com.example.financialplannerapp.data.local.dao.BillDao
import com.example.financialplannerapp.data.local.dao.WalletDao
import com.example.financialplannerapp.data.local.dao.BudgetDao
import com.example.financialplannerapp.data.local.dao.GoalDao
import com.example.financialplannerapp.data.local.model.AppSettingsEntity
import com.example.financialplannerapp.data.local.model.CategoryEntity
import com.example.financialplannerapp.data.local.model.ReceiptTransactionEntity
import com.example.financialplannerapp.data.local.model.SecurityEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.local.model.BudgetEntity
import com.example.financialplannerapp.data.local.model.GoalEntity

@Database(
    entities = [
        AppSettingsEntity::class,
        UserProfileEntity::class,
        SecurityEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        ReceiptTransactionEntity::class,
        BillEntity::class,
        WalletEntity::class,
        BudgetEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun securitySettingsDao(): SecuritySettingsDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun receiptTransactionDao(): ReceiptTransactionDao
    abstract fun billDao(): BillDao
    abstract fun walletDao(): WalletDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial-planner-db"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}