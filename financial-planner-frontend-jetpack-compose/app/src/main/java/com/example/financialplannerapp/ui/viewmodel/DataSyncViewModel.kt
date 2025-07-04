package com.example.financialplannerapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.model.toEntity
import com.example.financialplannerapp.data.model.toWalletData
import com.example.financialplannerapp.data.model.UserData
import com.example.financialplannerapp.data.local.model.UserProfileEntity
import com.example.financialplannerapp.data.repository.AppSettingsRepository
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.repository.WalletRepository
import com.example.financialplannerapp.data.repository.UserProfileRepository // Added
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull // Added
import java.text.SimpleDateFormat // Added for date parsing
import java.util.Locale // Added for date parsing
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Temporary mapper, ideally move to a common place
fun UserData.toUserProfileEntity(firebaseUid: String): UserProfileEntity {
    fun parseDateString(dateStr: String?): java.util.Date? {
        if (dateStr.isNullOrBlank()) return null
        val formats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        for (format in formats) {
            try {
                return format.parse(dateStr)
            } catch (e: java.text.ParseException) { /* Try next format */ }
        }
        return null
    }
    return UserProfileEntity(
        firebaseUid = firebaseUid, name = this.name, email = this.email, phone = this.phone,
        dateOfBirth = parseDateString(this.dateOfBirth), occupation = this.occupation,
        monthlyIncome = this.monthlyIncome?.toDoubleOrNull(), financialGoals = this.financialGoals,
        isDataModified = false
    )
}

class DataSyncViewModel(
    private val transactionRepository: TransactionRepository,
    private val receiptTransactionRepository: ReceiptTransactionRepository,
    private val walletRepository: WalletRepository,
    private val userProfileRepository: UserProfileRepository, // Added
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
            var messages = mutableListOf<String>()
            var overallSuccess = true

            try {
                val userEmail = tokenManager.getUserEmail() ?: "guest" // Used for wallets
                val firebaseUid = tokenManager.getUserId() // Assuming TokenManager can provide this for profile

                if (firebaseUid.isNullOrBlank()) {
                    messages.add("User not logged in. Cannot sync profile.")
                } else {
                    // 0. Sync Profile
                    try {
                        val remoteProfileResult = userProfileRepository.fetchRemoteUserProfile()
                        if (remoteProfileResult.isSuccess) {
                            val remoteUserData = remoteProfileResult.getOrNull()
                            if (remoteUserData != null) {
                                val entity = remoteUserData.toUserProfileEntity(firebaseUid)
                                val existingLocalProfile = userProfileRepository.getUserProfileByFirebaseUid(firebaseUid).firstOrNull()
                                if (existingLocalProfile != null) {
                                    userProfileRepository.updateUserProfile(entity.copy(id = existingLocalProfile.id))
                                } else {
                                    userProfileRepository.insertUserProfile(entity)
                                }
                                messages.add("Profile synced from server.")
                            } else {
                                messages.add("No profile data from server.")
                            }
                        } else {
                            messages.add("Failed to fetch remote profile: ${remoteProfileResult.exceptionOrNull()?.message}")
                            overallSuccess = false
                        }
                    } catch (e: Exception) {
                        messages.add("Error syncing profile: ${e.message}")
                        overallSuccess = false
                    }
                }


                // 1. Sync wallets
                try {
                    val localWallets = walletRepository.getWalletsByUserEmail(userEmail).first()
                    if (localWallets.isNotEmpty()) {
                        // Consider if local changes should be uploaded first or if backend is source of truth
                        // Current wallet sync in WalletRepositoryImpl's insertWallet seems to be backend-first for creation.
                        // uploadWalletsToBackend might be for a full push of local state.
                        // For now, let's assume backend is source of truth for sync.
                        // walletRepository.uploadWalletsToBackend(localWallets.map { it.toWalletData() })
                    }

                    val backendWallets = walletRepository.getWalletsFromBackend()
                    if (backendWallets.isNotEmpty()) {
                        walletRepository.insertWallets(backendWallets.map { it.toEntity() }) //This should handle upsert logic
                        messages.add("Wallets synced from server.")
                    } else {
                        messages.add("No wallets data from server or already up-to-date.")
                    }
                } catch (e: Exception) {
                    messages.add("Error syncing wallets: ${e.message}")
                    overallSuccess = false
                }

                // TODO: Add Transaction Sync if TransactionViewModel's syncAll is not sufficient or for a global sync button.
                // For now, Transaction sync is handled by TransactionViewModel.

                if (overallSuccess && messages.isEmpty()) {
                    _syncResult.value = "Sync complete. No changes or already up-to-date."
                } else if (overallSuccess) {
                    _syncResult.value = "Sync partially successful: ${messages.joinToString("; ")}"
                }
                else {
                    _syncResult.value = "Sync finished with issues: ${messages.joinToString("; ")}"
                }

            } catch (e: Exception) {
                _syncResult.value = "Overall sync failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearSyncResult() {
        _syncResult.value = null
    }
}