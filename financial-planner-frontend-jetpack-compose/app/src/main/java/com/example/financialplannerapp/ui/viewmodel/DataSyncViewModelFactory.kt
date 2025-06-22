package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.MainApplication

class DataSyncViewModelFactory(private val app: MainApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataSyncViewModel::class.java)) {
            val container = app.appContainer
            @Suppress("UNCHECKED_CAST")
            return DataSyncViewModel(
                transactionRepository = container.transactionRepository,
                receiptTransactionRepository = container.receiptTransactionRepository,
                walletRepository = container.walletRepository,
                tokenManager = container.tokenManager,
                appSettingsRepository = container.appSettingsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}