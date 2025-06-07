package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.service.ReceiptService

/**
 * Factory for creating ScanReceiptViewModel with required dependencies
 */
class ScanReceiptViewModelFactory(
    private val receiptService: ReceiptService,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanReceiptViewModel::class.java)) {
            return ScanReceiptViewModel(
                receiptService = receiptService,
                tokenManager = tokenManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
