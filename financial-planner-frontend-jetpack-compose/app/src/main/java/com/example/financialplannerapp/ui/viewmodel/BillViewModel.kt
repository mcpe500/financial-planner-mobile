package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.BillEntity
import com.example.financialplannerapp.data.repository.BillRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class BillUiState {
    object Loading : BillUiState()
    data class Success(val bills: List<BillEntity>) : BillUiState()
    data class Error(val message: String) : BillUiState()
    object Idle : BillUiState()
}

class BillViewModel(
    private val repository: BillRepository
) : ViewModel() {
    val localBills: StateFlow<List<BillEntity>> =
        repository.getAllBills().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow<BillUiState>(BillUiState.Idle)
    val uiState: StateFlow<BillUiState> = _uiState.asStateFlow()

    fun addBill(bill: BillEntity) {
        viewModelScope.launch {
            try {
                repository.insertBill(bill)
                _uiState.value = BillUiState.Success(repository.getAllBills().first())
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Error adding bill")
            }
        }
    }

    fun updateBill(bill: BillEntity) {
        viewModelScope.launch {
            try {
                repository.updateBill(bill)
                _uiState.value = BillUiState.Success(repository.getAllBills().first())
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Error updating bill")
            }
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            try {
                repository.deleteBill(bill)
                _uiState.value = BillUiState.Success(repository.getAllBills().first())
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Error deleting bill")
            }
        }
    }

    fun loadBills() {
        viewModelScope.launch {
            _uiState.value = BillUiState.Loading
            try {
                val bills = repository.getAllBills().first()
                _uiState.value = BillUiState.Success(bills)
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Error loading bills")
            }
        }
    }
} 