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
import java.util.Calendar
import java.text.SimpleDateFormat

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

                // 1. Create transaction for bill payment
                val transaction = TransactionEntity(
                    id = 0, // auto-generates
                    userId = tokenManager.getUserId() ?: "local_user",
                    amount = bill.estimatedAmount,
                    type = "EXPENSE", // Bill payments are always expenses
                    date = Date(),
                    pocket = "", // Empty as requested
                    category = "", // Empty as requested
                    note = "Payment for ${bill.name}",
                    walletId = wallet.id,
                    isFromReceipt = false,
                    receiptId = null,
                    merchantName = bill.name,
                    location = null,
                    receiptConfidence = null,
                    receipt_items = null,
                    isSynced = false
                )
                transactionRepository.insertTransaction(transaction)

                // 2. Update wallet balance
                val updatedWallet = wallet.copy(balance = wallet.balance - bill.estimatedAmount)
                walletRepository.updateWallet(updatedWallet)

                // 3. Calculate next due date based on repeat cycle
                val nextDueDate = calculateNextDueDate(bill.dueDate, bill.repeatCycle)

                // 4. Create new bill with updated due date
                val newBill = BillEntity(
                    uuid = UUID.randomUUID().toString(),
                    name = bill.name,
                    estimatedAmount = bill.estimatedAmount,
                    dueDate = nextDueDate,
                    repeatCycle = bill.repeatCycle,
                    category = bill.category,
                    notes = bill.notes,
                    isActive = bill.isActive,
                    paymentsJson = Gson().toJson(emptyList<Any>()),
                    autoPay = bill.autoPay,
                    notificationEnabled = bill.notificationEnabled,
                    lastPaymentDate = Date(),
                    creationDate = Date(),
                    userEmail = bill.userEmail
                )

                // 5. Insert new bill and delete old bill
                billRepository.insertBill(newBill)
                billRepository.deleteBillByUuid(bill.uuid)

                _operationSuccess.emit("Payment for ${bill.name} successful! New bill created for ${SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(nextDueDate)}")
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred during payment."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Calculate next due date based on repeat cycle
     */
    private fun calculateNextDueDate(currentDueDate: Date, repeatCycle: String): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDueDate
        
        when (repeatCycle.uppercase()) {
            "DAILY" -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
            "QUARTERLY" -> calendar.add(Calendar.MONTH, 3)
            "SEMI_ANNUALLY" -> calendar.add(Calendar.MONTH, 6)
            "ANNUALLY" -> calendar.add(Calendar.YEAR, 1)
            else -> calendar.add(Calendar.MONTH, 1) // Default to monthly
        }
        
        return calendar.time
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