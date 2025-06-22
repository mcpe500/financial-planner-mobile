package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.repository.BillRepository
import com.example.financialplannerapp.data.repository.GoalRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository

class DashboardViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val billRepository: BillRepository,
    private val walletRepository: WalletRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                transactionRepository,
                goalRepository,
                billRepository,
                walletRepository,
                tokenManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 