package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.data.repository.TransactionRepository

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 