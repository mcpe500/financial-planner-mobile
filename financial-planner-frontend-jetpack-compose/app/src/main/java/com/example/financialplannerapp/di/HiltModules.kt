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
import com.example.financialplannerapp.service.TranslationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "financial_planner_database"
        ).build()
    }

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }    @Provides
    fun provideSecuritySettingsDao(database: AppDatabase): SecuritySettingsDao {
        return database.securitySettingsDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileDao: UserProfileDao,
        apiService: ApiService
    ): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao, apiService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        dataStoreHelper: DataStoreHelper
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, dataStoreHelper)
    }    
    @Provides
    @Singleton
    fun provideAppSettingsRepository(
        appSettingsDao: AppSettingsDao
    ): AppSettingsRepository {
        return AppSettingsRepositoryImpl(appSettingsDao)
    }    @Provides
    @Singleton
    fun provideSecurityRepository(
        securitySettingsDao: SecuritySettingsDao
    ): SecurityRepository {
        return SecurityRepositoryImpl(securitySettingsDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {    
    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper {
        return DataStoreHelper(context)
    }

    @Provides
    @Singleton
    fun provideThemeService(): ThemeService {
        return ThemeService.getInstance()
    }

    @Provides
    @Singleton
    fun provideTranslationService(): TranslationService {
        return TranslationService.getInstance()
    }
}