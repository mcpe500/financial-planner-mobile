package com.example.financialplannerapp.di

import android.content.Context
import androidx.room.Room
import com.example.financialplannerapp.core.datastore.DataStoreHelper
import com.example.financialplannerapp.data.remote.RetrofitClient
import com.example.financialplannerapp.data.local.AppDatabase
import com.example.financialplannerapp.data.local.dao.AppSettingsDao
import com.example.financialplannerapp.data.local.dao.UserProfileDao
import com.example.financialplannerapp.data.local.dao.SecuritySettingsDao
import com.example.financialplannerapp.data.remote.ApiService
import com.example.financialplannerapp.data.repository.AppSettingsRepository
import com.example.financialplannerapp.data.repository.AppSettingsRepositoryImpl
import com.example.financialplannerapp.data.repository.AuthRepository
import com.example.financialplannerapp.data.repository.AuthRepositoryImpl
import com.example.financialplannerapp.data.repository.SecurityRepository
import com.example.financialplannerapp.data.repository.SecurityRepositoryImpl
import com.example.financialplannerapp.data.repository.UserProfileRepository
import com.example.financialplannerapp.data.repository.UserProfileRepositoryImpl
import com.example.financialplannerapp.service.ThemeService
import com.example.financialplannerapp.data.model.TranslationProvider
import com.example.financialplannerapp.service.TranslationServiceImpl

object DatabaseModule {
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "financial_planner_database"
        ).build()
    }

    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }

    fun provideSecuritySettingsDao(database: AppDatabase): SecuritySettingsDao {
        return database.securitySettingsDao()
    }
}

object NetworkModule {
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }
}

object RepositoryModule {
    fun provideUserProfileRepository(
        userProfileDao: UserProfileDao,
        apiService: ApiService
    ): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao, apiService)
    }

    fun provideAuthRepository(
        apiService: ApiService,
        dataStoreHelper: DataStoreHelper
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, dataStoreHelper)
    }

    fun provideAppSettingsRepository(
        appSettingsDao: AppSettingsDao
    ): AppSettingsRepository {
        return AppSettingsRepositoryImpl(appSettingsDao)
    }

    fun provideSecurityRepository(
        securitySettingsDao: SecuritySettingsDao
    ): SecurityRepository {
        return SecurityRepositoryImpl(securitySettingsDao)
    }
}

object AppServicesModule {
    fun provideDataStoreHelper(context: Context): DataStoreHelper {
        return DataStoreHelper(context)
    }

    fun provideTranslationService(context: Context): TranslationProvider {
        return TranslationServiceImpl(context)
    }

    fun provideThemeService(dataStoreHelper: DataStoreHelper): ThemeService {
        return ThemeService(dataStoreHelper)
    }
}