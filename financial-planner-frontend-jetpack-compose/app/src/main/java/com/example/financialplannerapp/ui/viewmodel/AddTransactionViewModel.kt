package com.example.financialplannerapp.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _state = mutableStateOf(AddTransactionState())
    val state: State<AddTransactionState> = _state

    fun createTransaction(
        userId: String, // Removed default value
        amount: Double,
        type: String,
        date: Date,
        pocket: String, // Consider if 'pocket' is still relevant or if walletId covers this
        category: String,
        note: String?,
        tags: List<String>?,
        walletId: String, // Removed default value
        isFromReceipt: Boolean = false,
        receiptId: String? = null,
        merchantName: String? = null,
        location: String? = null,
        receiptImagePath: String? = null,
        receiptConfidence: Double? = null
    ) {
        _state.value = _state.value.copy(isLoading = true, isSuccess = false, error = null)
        val transaction = TransactionEntity(
            userId = userId,
            amount = amount,
            type = type,
            date = date,
            pocket = pocket,
            category = category,
            note = note,
            tags = tags,
            walletId = walletId,
            isFromReceipt = isFromReceipt,
            receiptId = receiptId,
            merchantName = merchantName,
            location = location,
            receiptImagePath = receiptImagePath,
            receiptConfidence = receiptConfidence
        )
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                _state.value = _state.value.copy(isLoading = false, isSuccess = true, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, isSuccess = false, error = e.message)
            }
        }
    }

    fun resetState() {
        _state.value = AddTransactionState()
    }
}

data class AddTransactionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)