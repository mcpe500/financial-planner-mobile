package com.example.financialplannerapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.financialplannerapp.data.UserProfileDatabaseHelper
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.remote.RetrofitClient
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
import com.example.financialplannerapp.data.repository.UserProfileRoomRepositoryImpl
import com.example.financialplannerapp.service.LocalSettingsService
import com.example.financialplannerapp.data.model.TranslationProvider
import com.example.financialplannerapp.data.repository.UserProfileRoomRepository
import com.example.financialplannerapp.service.TranslationServiceImpl
import com.example.financialplannerapp.service.ReactiveSettingsService
import com.example.financialplannerapp.data.AppSettingsDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainApplication : Application() {

    lateinit var appContainer: AppContainer
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        
        initializeServices()
    }
    
    private fun initializeServices() {
        applicationScope.launch {
            try {
                // Ensure AppSettingsDatabaseHelper is initialized before ReactiveSettingsService
                val settingsHelper = appContainer.appSettingsDatabaseHelper // Get from AppContainer
                val settingsService = appContainer.reactiveSettingsService // Get from AppContainer
                settingsService.initialize(settingsHelper) // Initialize with the helper
            } catch (e: Exception) {
                // Log the exception or handle it appropriately
                e.printStackTrace()
            }
        }
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

    // TokenManager
    val tokenManager: TokenManager by lazy {
        TokenManager(applicationContext)
    }

    // API Service
    private val apiService = RetrofitClient.apiService

    // DAOs from AppDatabase - only use DAOs that actually exist
    private val transactionDao by lazy { appDatabase.transactionDao() }
    private val categoryDao by lazy { appDatabase.categoryDao() }
    private val appSettingsDao by lazy { appDatabase.appSettingsDao() }
    private val userProfileDao by lazy { appDatabase.userProfileDao() }
    private val securitySettingsDao by lazy { appDatabase.securitySettingsDao() }

    // Services
    val translationProvider: TranslationProvider by lazy {
        TranslationServiceImpl(applicationContext) // Corrected: Pass applicationContext
    }

    val localSettingsService: LocalSettingsService by lazy {
        LocalSettingsService.getInstance() // Assuming this is a singleton without context or initialized elsewhere
    }
    
    val appSettingsDatabaseHelper: AppSettingsDatabaseHelper by lazy {
        AppSettingsDatabaseHelper.getInstance(applicationContext)
    }
    
    val reactiveSettingsService: ReactiveSettingsService by lazy {
        ReactiveSettingsService.getInstance() // Assuming this is a singleton and initialized in MainApplication
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, com.example.financialplannerapp.core.datastore.DataStoreHelper(applicationContext))
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(categoryDao, apiService)
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(transactionDao, apiService)
    }

    private val userProfileDatabaseHelper: UserProfileDatabaseHelper by lazy {
        UserProfileDatabaseHelper(applicationContext)
    }
    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepositoryImpl(userProfileDao, apiService)
    }

    val userProfileRoomRepository: UserProfileRoomRepository by lazy {
        UserProfileRoomRepositoryImpl(userProfileDao)
    }
    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(securitySettingsDao)
    }
    val appSettingsRepository: AppSettingsRepository by lazy {
        AppSettingsRepositoryImpl(appSettingsDao)
    }
    
    // Receipt Service
    val receiptService: com.example.financialplannerapp.data.service.ReceiptService by lazy {
        com.example.financialplannerapp.data.service.ReceiptService(apiService, tokenManager, applicationContext)
    }
}
