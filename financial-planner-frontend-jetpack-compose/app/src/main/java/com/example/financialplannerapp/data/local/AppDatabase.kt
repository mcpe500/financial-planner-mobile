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
import com.example.financialplannerapp.data.local.model.AppSettingsEntity
import com.example.financialplannerapp.data.local.model.CategoryEntity
import com.example.financialplannerapp.data.local.model.ReceiptTransactionEntity
import com.example.financialplannerapp.data.local.model.SecurityEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.local.model.WalletEntity

@Database(
    entities = [
        AppSettingsEntity::class,
        UserProfileEntity::class,
        SecurityEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        ReceiptTransactionEntity::class,
        BillEntity::class,
        WalletEntity::class
    ],
    version = 8,
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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE bills ADD COLUMN user_email TEXT NOT NULL DEFAULT 'guest'")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Rename user_id column to user_email in wallets table
                database.execSQL("ALTER TABLE wallets RENAME TO wallets_old")
                database.execSQL("""
                    CREATE TABLE wallets (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        balance REAL NOT NULL,
                        color_hex TEXT NOT NULL,
                        user_email TEXT NOT NULL,
                        icon_name TEXT NOT NULL
                    )
                """)
                database.execSQL("""
                    INSERT INTO wallets (id, name, type, balance, color_hex, user_email, icon_name)
                    SELECT id, name, type, balance, color_hex, user_id, icon_name FROM wallets_old
                """)
                database.execSQL("DROP TABLE wallets_old")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_planner_db"
                )
                .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}