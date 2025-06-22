package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.BillRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class BillViewModel(
    private val billRepository: BillRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val localBills: StateFlow<List<BillEntity>> =
        billRepository.getAllBills()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val userEmail = if (tokenManager.isNoAccountMode()) {
        "guest"
    } else {
        tokenManager.getUserEmail() ?: "guest"
    }
    val wallets: StateFlow<List<WalletEntity>> =
        walletRepository.getWalletsByUserEmail(userEmail)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableSharedFlow<String>()
    val operationSuccess: SharedFlow<String> = _operationSuccess.asSharedFlow()

    fun payBill(bill: BillEntity, wallet: WalletEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                if (wallet.balance < bill.estimatedAmount) {
                    throw Exception("Insufficient wallet balance.")
                }

                // 1. Create transaction
                val transaction = TransactionEntity(
                    id = 0, // auto-generates
                    userId = tokenManager.getUserId() ?: "", // Make sure to handle this properly
                    amount = bill.estimatedAmount,
                    type = "expense",
                    category = "",
                    date = Date(),
                    note = "Payment for ${bill.name}",
                    walletId = wallet.id,
                    pocket = ""
                )
                transactionRepository.insertTransaction(transaction)

                // 2. Update wallet balance
                val updatedWallet = wallet.copy(balance = wallet.balance - bill.estimatedAmount)
                walletRepository.updateWallet(updatedWallet)

                // 3. Update bill status (assuming isPaid field exists)
                // Let's assume for a recurring bill, we don't just mark it as "paid",
                // but we record a payment. The BillEntity has `paymentsJson`.
                // Let's add a simple payment record.
                val payment = mapOf("date" to Date().time, "amount" to bill.estimatedAmount)
                val type = object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
                val payments = Gson().fromJson<List<Map<String, Any>>>(bill.paymentsJson, type).toMutableList()
                payments.add(payment)
                val updatedBill = bill.copy(paymentsJson = Gson().toJson(payments), lastPaymentDate = Date())

                // A better approach for recurring bills would be to check if a payment for the current
                // period has been made. For now, we just add to the payment history.
                // If there was an isPaid flag, we'd set it here.
                billRepository.updateBill(updatedBill)


                _operationSuccess.emit("Payment for ${bill.name} successful!")
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred during payment."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBill(
        name: String,
        estimatedAmount: Double,
        dueDate: Date,
        repeatCycle: String,
        category: String?,
        notes: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userEmail = if (tokenManager.isNoAccountMode()) {
                    "guest"
                } else {
                    tokenManager.getUserEmail() ?: "guest"
                }

                val bill = BillEntity(
                    uuid = UUID.randomUUID().toString(),
                    name = name,
                    estimatedAmount = estimatedAmount,
                    dueDate = dueDate,
                    repeatCycle = repeatCycle,
                    category = category,
                    notes = notes,
                    isActive = true,
                    paymentsJson = Gson().toJson(emptyList<Any>()),
                    autoPay = false,
                    notificationEnabled = true,
                    lastPaymentDate = null,
                    creationDate = Date(),
                    userEmail = userEmail
                )

                billRepository.insertBill(bill)
                _operationSuccess.emit("Bill added successfully!")
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred while adding the bill."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBill(bill: BillEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                billRepository.updateBill(bill)
                _operationSuccess.emit("Bill updated successfully!")
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred while updating the bill."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBill(billId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                billRepository.deleteBillByUuid(billId)
                _operationSuccess.emit("Bill deleted successfully!")
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred while deleting the bill."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}