package com.example.financialplannerapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.financialplannerapp.data.SecurityDatabaseHelper
import com.example.financialplannerapp.data.UserProfileDatabaseHelper
import com.example.financialplannerapp.data.repository.AppSettingsRepository
import com.example.financialplannerapp.data.repository.AppSettingsRepositoryImpl
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.data.repository.AuthRepositoryImpl
import com.example.financialplannerapp.data.repository.CategoryRepository
import com.example.financialplannerapp.data.repository.CategoryRepositoryImpl
import com.example.financialplannerapp.data.repository.SecurityRepository
import com.example.financialplannerapp.data.repository.SecurityRepositoryImpl
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.TransactionRepositoryImpl
import com.example.financialplannerapp.data.repository.UserProfileRepository
import com.example.financialplannerapp.data.repository.UserProfileRepositoryImpl
import com.example.financialplannerapp.data.repository.UserProfileRoomRepository
import com.example.financialplannerapp.data.repository.UserProfileRoomRepositoryImpl
import com.example.financialplannerapp.service.LocalSettingsService
import com.example.financialplannerapp.service.TranslationProvider
import com.example.financialplannerapp.service.TranslationServiceImpl
import com.example.financialplannerapp.data.network.RetrofitInstance

class MainApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

class AppContainer(private val applicationContext: Context) {

    // Database
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "financial_planner_db"
        ).build()
    }

    // DAOs from AppDatabase
    private val transactionDao by lazy { appDatabase.transactionDao() }
    private val categoryDao by lazy { appDatabase.categoryDao() }
    private val recurringBillDao by lazy { appDatabase.recurringBillDao() }
    private val budgetDao by lazy { appDatabase.budgetDao() }
    private val goalDao by lazy { appDatabase.goalDao() }
    private val appSettingsDao by lazy { appDatabase.appSettingsDao() }
    private val userDao by lazy { appDatabase.userDao() }

    // Services
    val translationProvider: TranslationProvider by lazy {
        TranslationServiceImpl()
    }

    val localSettingsService: LocalSettingsService by lazy {
        LocalSettingsService(appSettingsDao, translationProvider)
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(RetrofitInstance.authApiService)
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(categoryDao)
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(transactionDao)
    }

    private val userProfileDatabaseHelper: UserProfileDatabaseHelper by lazy {
        UserProfileDatabaseHelper(applicationContext)
    }
    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepositoryImpl(userProfileDatabaseHelper)
    }

    val userProfileRoomRepository: UserProfileRoomRepository by lazy {
        UserProfileRoomRepositoryImpl(userDao)
    }

    private val securityDatabaseHelper: SecurityDatabaseHelper by lazy {
        SecurityDatabaseHelper(applicationContext)
    }
    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(securityDatabaseHelper)
    }

    val appSettingsRepository: AppSettingsRepository by lazy {
        AppSettingsRepositoryImpl(appSettingsDao)
    }
}
