package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.financialplannerapp.data.model.toNetworkModel
import com.example.financialplannerapp.data.model.toEntity
import android.util.Log

class DataSyncViewModel(
    private val transactionRepository: TransactionRepository,
    private val receiptTransactionRepository: ReceiptTransactionRepository,
    private val tokenManager: TokenManager,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _syncResult = MutableStateFlow<String?>(null)
    val syncResult: StateFlow<String?> = _syncResult.asStateFlow()

    fun checkBackendConnectivity() {
        viewModelScope.launch {
            _isConnected.value = appSettingsRepository.checkBackendHealth()
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            Log.d("DataSync", "Starting syncAll()...")
            _isSyncing.value = true
            _syncResult.value = null
            try {
                val token = tokenManager.getToken() ?: throw Exception("No token")
                val userId = tokenManager.getUserId() ?: "local_user"

                // 1. Upload unsynced local transactions
                val unsynced = transactionRepository.getUnsyncedTransactions(userId)
                Log.d("DataSync", "Unsynced local transactions: ${unsynced.size}")
                if (unsynced.isNotEmpty()) {
                    val uploadSuccess = transactionRepository.uploadTransactionsToBackend(
                        unsynced.map { it.toNetworkModel() }
                    )
                    Log.d("DataSync", "Upload to backend success: $uploadSuccess")
                    if (uploadSuccess) {
                        transactionRepository.markTransactionsAsSynced(unsynced.map { it.id })
                        Log.d("DataSync", "Marked transactions as synced: ${unsynced.map { it.id }}")
                    }
                }

                // 2. Download latest transactions from backend
                val backendTransactions = transactionRepository.getTransactionsFromBackend()
                Log.d("DataSync", "Downloaded ${backendTransactions.size} transactions from backend")
                val entities = backendTransactions.map { it.toEntity(userId) }
                Log.d("DataSync", "Mapped backend transactions to entities: ${entities.size}")
                transactionRepository.insertTransactions(entities)
                Log.d("DataSync", "Inserted backend transactions to RoomDB")

                _lastSyncTime.value = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
                _syncResult.value = "Sinkronisasi berhasil"
                Log.d("DataSync", "Sync finished successfully")
            } catch (e: Exception) {
                _syncResult.value = "Gagal sinkronisasi: ${e.message}"
                Log.e("DataSync", "Sync failed: ${e.message}", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }
} 