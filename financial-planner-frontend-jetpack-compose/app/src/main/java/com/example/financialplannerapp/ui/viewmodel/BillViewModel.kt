package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.repository.BillRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class BillViewModel(
    private val repository: BillRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val localBills: StateFlow<List<BillEntity>> =
        repository.getAllBills()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _operationSuccess = MutableSharedFlow<Boolean>()
    val operationSuccess: SharedFlow<Boolean> = _operationSuccess.asSharedFlow()

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

                repository.insertBill(bill)
                _operationSuccess.emit(true)
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred while adding the bill."
                _operationSuccess.emit(false)
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
                repository.updateBill(bill)
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
                repository.deleteBillByUuid(billId)
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