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
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepository
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepositoryImpl
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
import com.example.financialplannerapp.data.repository.BillRepository
import com.example.financialplannerapp.data.repository.BillRepositoryImpl
import com.example.financialplannerapp.data.repository.BudgetRepository
import com.example.financialplannerapp.data.repository.BudgetRepositoryImpl
import com.example.financialplannerapp.data.repository.GoalRepository
import com.example.financialplannerapp.data.repository.GoalRepositoryImpl
import com.example.financialplannerapp.data.repository.WalletRepository
import com.example.financialplannerapp.data.repository.WalletRepositoryImpl

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
            AppDatabase::class.java, "financial-planner-db"
        ).build()
    }

    // TokenManager
    val tokenManager: TokenManager by lazy {
        TokenManager(applicationContext)
    }

    // API Service
    val apiService by lazy { RetrofitClient.getApiService(applicationContext) }

    // DAOs from AppDatabase - only use DAOs that actually exist
    private val transactionDao by lazy { appDatabase.transactionDao() }
    private val receiptTransactionDao by lazy { appDatabase.receiptTransactionDao() }
    private val appSettingsDao by lazy { appDatabase.appSettingsDao() }
    private val userProfileDao by lazy { appDatabase.userProfileDao() }
    private val securitySettingsDao by lazy { appDatabase.securitySettingsDao() }
    private val billDao by lazy { appDatabase.billDao() }
    private val walletDao by lazy { appDatabase.walletDao() }
    private val budgetDao by lazy { appDatabase.budgetDao() }
    private val goalDao by lazy { appDatabase.goalDao() }

    // Services
    val translationProvider: TranslationProvider by lazy {
        TranslationServiceImpl(applicationContext) // Corrected: Pass applicationContext
    }

    val localSettingsService: LocalSettingsService by lazy {
        LocalSettingsService.getInstance() // Assuming this is a singleton without context or initialized elsewhere
    }
    
    val appSettingsDatabaseHelper: AppSettingsDatabaseHelper by lazy {
        AppSettingsDatabaseHelper.getInstance(applicationContext, apiService)
    }
    
    val reactiveSettingsService: ReactiveSettingsService by lazy {
        ReactiveSettingsService.getInstance() // Assuming this is a singleton and initialized in MainApplication
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, com.example.financialplannerapp.core.datastore.DataStoreHelper(applicationContext))
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(transactionDao, apiService)
    }

    val receiptTransactionRepository: ReceiptTransactionRepository by lazy {
        ReceiptTransactionRepositoryImpl(receiptTransactionDao, transactionDao, apiService)
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
        AppSettingsRepositoryImpl(appSettingsDao, apiService)
    }
    
    // Receipt Service
    val receiptService: com.example.financialplannerapp.data.service.ReceiptService by lazy {
        com.example.financialplannerapp.data.service.ReceiptService(apiService, tokenManager, applicationContext)
    }

    val billRepository: BillRepository by lazy {
        BillRepositoryImpl(billDao)
    }

    val budgetRepository: BudgetRepository by lazy {
        BudgetRepositoryImpl(budgetDao)
    }

    val goalRepository: GoalRepository by lazy {
        GoalRepositoryImpl(goalDao)
    }

    val walletRepository: WalletRepository by lazy {
        WalletRepositoryImpl(walletDao)
    }
}
