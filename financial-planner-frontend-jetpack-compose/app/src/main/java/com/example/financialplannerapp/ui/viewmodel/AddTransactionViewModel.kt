package com.example.financialplannerapp.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = mutableStateOf(AddTransactionState())
    val state: State<AddTransactionState> = _state

    fun createTransaction(
        userId: String = "current_user_id",
        amount: Double,
        type: String,
        date: Date,
        pocket: String,
        category: String,
        note: String?,
        tags: List<String>?,
        walletId: String = "default_wallet_id",
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
                // Insert transaction
                val transactionId = transactionRepository.insertTransaction(transaction)
                
                // Update wallet balance
                updateWalletBalance(walletId, amount, type)
                
                _state.value = _state.value.copy(isLoading = false, isSuccess = true, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, isSuccess = false, error = e.message)
            }
        }
    }

    /**
     * Update wallet balance after transaction
     */
    private suspend fun updateWalletBalance(walletId: String, amount: Double, type: String) {
        try {
            // Get current wallet from the wallets list
            val userEmail = tokenManager.getUserEmail() ?: "local_user@example.com"
            val walletsFlow = walletRepository.getWalletsByUserEmail(userEmail)
            val wallets = walletsFlow.first()
            val currentWallet = wallets.find { it.id == walletId }
            
            currentWallet?.let { wallet ->
                val newBalance = if (type == "INCOME") {
                    wallet.balance + amount // Add for income
                } else {
                    wallet.balance - amount // Subtract for expense
                }
                val updatedWallet = wallet.copy(balance = newBalance)
                walletRepository.updateWallet(updatedWallet)
            }
        } catch (e: Exception) {
            // Log error but don't fail the transaction
            println("Failed to update wallet balance: ${e.message}")
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