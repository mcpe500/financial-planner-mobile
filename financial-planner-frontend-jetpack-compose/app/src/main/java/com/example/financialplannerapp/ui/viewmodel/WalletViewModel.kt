// file: D:/KULIAH/MDP/project_mdp/financial-planner-mobile/financial-planner-frontend-jetpack-compose/app/src/main/java/com/example/financialplannerapp/presentation/viewmodel/WalletViewModel.kt

package com.example.financialplannerapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.data.local.model.WalletEntity
import com.example.financialplannerapp.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color.Companion.Transparent
//import androidx.compose.ui.graphics.toColor

import com.example.financialplannerapp.ui.model.Wallet
import com.example.financialplannerapp.ui.model.WalletType
import com.example.financialplannerapp.ui.model.iconFromName
import com.example.financialplannerapp.ui.model.toColor
import com.example.financialplannerapp.ui.model.toHex

class WalletViewModel(
    private val walletRepository: WalletRepository,
    private val userId: String // Assuming user ID is passed to get user-specific wallets
) : ViewModel() {

    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets: StateFlow<List<Wallet>> = _wallets.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadWallets()
    }

    private fun loadWallets() {
        viewModelScope.launch {
            _isLoading.value = true
            walletRepository.getAllWallets() // Pass the userId here!
                .catch { e ->
                    _error.value = "Error loading wallets: ${e.localizedMessage}"
                    _isLoading.value = false
                }
                .collect { walletEntities ->
                    _wallets.value = walletEntities.map { it.toWalletUiModel() }
                    _isLoading.value = false
                }
        }
    }
    // --- THIS IS THE FUNCTION TO CHECK/CHANGE ---
    fun addWallet(wallet: Wallet) {
        // WalletViewModel.kt
        viewModelScope.launch {
            try {
                val rowId = walletRepository.insertWallet(wallet.toWalletEntity(userId))
                if (rowId != -1L) { // Umumnya, -1 berarti gagal insert (misal: karena IGNORE dan ada konflik)
                    _error.value = "Wallet added successfully!"
                    Log.d("WalletViewModel", "Wallet inserted successfully with ID: $rowId")
                } else {
                    _error.value = "Wallet insertion failed (possible conflict)!"
                    Log.e("WalletViewModel", "Wallet insertion returned -1, likely a conflict.")
                }
            } catch (e: Exception) {
                _error.value = "Error adding wallet: ${e.localizedMessage}"
                Log.e("WalletViewModel", "Exception during wallet insert: ${e.message}", e)
            }
        }
    }


    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch {
            try {
                // Pass the current userId to the mapping function
                walletRepository.updateWallet(wallet.toWalletEntity(userId))
            } catch (e: Exception) {
                _error.value = "Error updating wallet: ${e.localizedMessage}"
            }
        }
    }

    fun deleteWallet(walletId: String) {
        viewModelScope.launch {
            try {
                walletRepository.deleteWalletById(walletId)
            } catch (e: Exception) {
                _error.value = "Error deleting wallet: ${e.localizedMessage}"
            }
        }
    }
}

// --- Mapping Extensions ---
// These extensions convert between your database entity and your UI model
// They must be defined OUTSIDE the ViewModel class (e.g., in WalletUiModels.kt, or a dedicated mapping file)
// For this example, if WalletUiModels.kt already exists and includes Wallet data class,
// then these mapping functions can stay in a separate file (e.g., WalletMappers.kt) or be placed directly in WalletUiModels.kt.
// Given your current ViewModel, it implies these are external functions.

fun WalletEntity.toWalletUiModel(): Wallet {
    return Wallet(
        id = this.id,
        name = this.name,
        type = try { WalletType.valueOf(this.type.uppercase()) } catch (e: IllegalArgumentException) { WalletType.CASH },
        balance = this.balance,
        color = this.colorHex.toColor(), // Use the toColor extension from WalletUiModels.kt
        isShared = this.isShared,
        memberCount = this.memberCount,
        icon = iconFromName(this.iconName) // Use the iconFromName helper from WalletUiModels.kt
    )
}

fun Wallet.toWalletEntity(userId: String): WalletEntity {
    return WalletEntity(
        id = this.id,
        name = this.name,
        type = this.type.name, // Convert enum to String
        balance = this.balance,
        colorHex = this.color.toHex(), // Use the toHex extension from WalletUiModels.kt
        isShared = this.isShared,
        memberCount = this.memberCount,
        userId = userId, // Pass the current user ID
        iconName = this.icon.name // Store ImageVector's name as a string
    )
}