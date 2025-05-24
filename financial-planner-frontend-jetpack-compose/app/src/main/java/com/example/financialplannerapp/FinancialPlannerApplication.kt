package com.example.financialplannerapp

import android.app.Application
import android.util.Log
import com.example.financialplannerapp.utils.AppDatabase
import com.example.financialplannerapp.utils.TokenManager

class FinancialPlannerApplication : Application() {

    val TAG = "FinancialPlannerApp"

    // Lazily initialize AppDatabase and TokenManager
    // This ensures they are created only when first accessed and only once.
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val tokenManager: TokenManager by lazy { TokenManager(this) }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FinancialPlannerApplication onCreate")
        // Initialize other app-wide components here if needed
        // For example, setting up dependency injection (Hilt, Koin),
        // initializing logging libraries, etc.

        // Pre-warm the database (optional, but can help with first access speed)
        // This is just an example; you might not need it.
        // lifecycleScope.launch { database.userProfileDao().getUserProfileById("-1") } // Example query
    }
}