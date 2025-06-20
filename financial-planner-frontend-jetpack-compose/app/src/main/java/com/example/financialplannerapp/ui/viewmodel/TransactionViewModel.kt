package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.TransactionEntity
import com.example.financialplannerapp.data.model.TransactionData
import com.example.financialplannerapp.data.model.TransactionPayload
import com.example.financialplannerapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val transactions: List<TransactionData>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
    object Idle : TransactionUiState()
}

class TransactionViewModel(
    private val repository: TransactionRepository,
    private val tokenProvider: () -> String,
    private val userIdProvider: () -> String
) : ViewModel() {
    // Data lokal (Room/SQLite)
    val localTransactions: StateFlow<List<TransactionEntity>> =
        repository.getAllTransactions().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Data remote (Supabase)
    private val _remoteUiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
    val remoteUiState: StateFlow<TransactionUiState> = _remoteUiState.asStateFlow()

    // Sync dari Supabase ke Room
    fun syncFromRemote() {
        viewModelScope.launch {
            _remoteUiState.value = TransactionUiState.Loading
            try {
                val userId = userIdProvider()
                val result = repository.syncTransactionsFromRemote(userId)
                result.fold(
                    onSuccess = { _remoteUiState.value = TransactionUiState.Success(it) },
                    onFailure = { _remoteUiState.value = TransactionUiState.Error(it.message ?: "Sync error") }
                )
            } catch (e: Exception) {
                _remoteUiState.value = TransactionUiState.Error(e.message ?: "Sync error")
            }
        }
    }

    // Tambah transaksi (lokal & remote)
    fun addTransactionLocalAndRemote(entity: TransactionEntity, payload: TransactionPayload) {
        viewModelScope.launch {
            repository.insertTransaction(entity) // Simpan ke Room
            try {
                val token = tokenProvider()
                repository.createTransactionRemote(token, payload) // Simpan ke Supabase
            } catch (_: Exception) {
                // Bisa tambahkan log/flag untuk retry sync jika gagal
            }
        }
    }
} 