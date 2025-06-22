package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.model.toEntity
import com.example.financialplannerapp.data.model.toWalletData
import com.example.financialplannerapp.data.repository.AppSettingsRepository
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DataSyncViewModel(
    private val transactionRepository: TransactionRepository,
    private val receiptTransactionRepository: ReceiptTransactionRepository,
    private val walletRepository: WalletRepository,
    private val tokenManager: TokenManager,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _syncResult = MutableStateFlow<String?>(null)
    val syncResult: StateFlow<String?> = _syncResult.asStateFlow()

    fun checkBackendConnectivity() {
        viewModelScope.launch {
            try {
                // Simple connectivity check
                val wallets = walletRepository.getWalletsFromBackend()
                _isConnected.value = true
            } catch (e: Exception) {
                _isConnected.value = false
            }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncResult.value = null
            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest"

                // 1. Sync wallets
                val localWallets = walletRepository.getWalletsByUserEmail(userEmail).first()
                if (localWallets.isNotEmpty()) {
                    walletRepository.uploadWalletsToBackend(
                        localWallets.map { it.toWalletData() }
                    )
                }

                val backendWallets = walletRepository.getWalletsFromBackend()
                walletRepository.insertWallets(backendWallets.map { it.toEntity() })

                _syncResult.value = "Sync successful"
            } catch (e: Exception) {
                _syncResult.value = "Sync failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearSyncResult() {
        _syncResult.value = null
    }
}