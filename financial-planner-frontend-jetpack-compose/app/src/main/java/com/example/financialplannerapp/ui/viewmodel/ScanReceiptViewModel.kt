package com.example.financialplannerapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialplannerapp.TokenManager
import com.example.financialplannerapp.data.model.ReceiptOCRState
import com.example.financialplannerapp.data.model.TransactionFromOCR
import com.example.financialplannerapp.data.repository.ReceiptTransactionRepository
import com.example.financialplannerapp.data.repository.TransactionRepository
import com.example.financialplannerapp.data.service.ReceiptService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for managing receipt scanning and OCR processing functionality.
 * Handles image capture, processing, and extracted transaction data.
 */
class ScanReceiptViewModel(
    private val receiptService: ReceiptService,
    private val tokenManager: TokenManager,
    private val receiptTransactionRepository: ReceiptTransactionRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ScanReceiptViewModel"
    }

    // UI state management using the proper state sealed class
    private val _state = MutableStateFlow<ReceiptOCRState>(ReceiptOCRState.Idle)
    val state: StateFlow<ReceiptOCRState> = _state.asStateFlow()

    // Current image URI for camera capture
    var currentImageUri: Uri? = null
        private set

    /**
     * Create a file URI for camera capture
     */
    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
        currentImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        return currentImageUri!!
    }

    /**
     * Process the receipt image using OCR
     */
    fun processReceipt(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _state.value = ReceiptOCRState.Processing
                Log.d(TAG, "Starting receipt processing for image: $imageUri")

                val result = receiptService.processReceiptOCR(imageUri)

                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "OCR processing successful. Found ${response.data.items.size} items")
                        _state.value = ReceiptOCRState.Success(response.data)

                        // Save to RoomDB as a transaction
                        saveOcrResultToTransaction(response.data)
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Unknown error occurred"
                        Log.e(TAG, "OCR processing failed: $errorMessage", exception)
                        _state.value = ReceiptOCRState.Error(errorMessage)
                    }
                )
            } catch (e: Exception) {
                val errorMessage = "Unexpected error during receipt processing: ${e.message}"
                Log.e(TAG, errorMessage, e)
                _state.value = ReceiptOCRState.Error(errorMessage)
            }
        }
    }

    /**
     * Reset state to idle for new scan
     */
    fun resetState() {
        _state.value = ReceiptOCRState.Idle
        currentImageUri = null
        Log.d(TAG, "State reset for new scan")
    }

    /**
     * Retry the last OCR processing
     */
    fun retryProcessing() {
        currentImageUri?.let { uri ->
            processReceipt(uri)
        } ?: run {
            Log.w(TAG, "No image URI available for retry")
        }
    }

    /**
     * Convert OCR data to transaction format for saving
     */
    fun convertToTransactions(ocrData: com.example.financialplannerapp.data.model.ReceiptOCRData): List<TransactionFromOCR> {
        return listOf(
            TransactionFromOCR(
                amount = ocrData.totalAmount,
                merchant = ocrData.merchantName,
                date = ocrData.date,
                items = ocrData.items,
                receiptId = ocrData.receiptId ?: "receipt_${System.currentTimeMillis()}"
            )
        )
    }

    /**
     * Store OCR data both locally (Room) and remotely (Supabase)
     * This handles both authenticated and local users
     */
    fun storeOCRData(ocrData: com.example.financialplannerapp.data.model.ReceiptOCRData) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId() ?: "local_user"
                val isAuthenticated = tokenManager.isAuthenticated()
                
                Log.d(TAG, "Storing OCR data for user: $userId, authenticated: $isAuthenticated")
                
                // Always store in local Room database
                val localResult = receiptTransactionRepository.storeReceiptTransactionFromOCR(
                    ocrData = ocrData,
                    userId = userId
                )
                
                localResult.fold(
                    onSuccess = { receiptTransaction ->
                        Log.d(TAG, "OCR data stored locally with ID: ${receiptTransaction.id}")
                        
                        // If user is authenticated, also sync to backend
                        if (isAuthenticated) {
                            // Sync to backend, then convert to regular transaction
                            val syncResult = receiptTransactionRepository.syncReceiptTransactionToBackend(receiptTransaction)
                            syncResult.fold(
                                onSuccess = { backendId ->
                                    Log.d(TAG, "Receipt transaction synced successfully with backend ID: $backendId")
                                    // Now convert to regular transaction in RoomDB
                                    convertReceiptToTransaction(receiptTransaction.id)
                                },
                                onFailure = { exception ->
                                    Log.e(TAG, "Failed to sync to backend: ${exception.message}", exception)
                                    // Still convert to regular transaction in RoomDB
                                    convertReceiptToTransaction(receiptTransaction.id)
                                }
                            )
                        } else {
                            Log.d(TAG, "User not authenticated, data stored only locally")
                            // Convert to regular transaction in RoomDB
                            convertReceiptToTransaction(receiptTransaction.id)
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to store OCR data locally: ${exception.message}", exception)
                        _state.value = ReceiptOCRState.Error("Failed to save receipt data: ${exception.message}")
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error storing OCR data: ${e.message}", e)
                _state.value = ReceiptOCRState.Error("Unexpected error saving receipt data")
            }
        }
    }

    private suspend fun convertReceiptToTransaction(receiptTransactionId: Int) {
        try {
            val result = receiptTransactionRepository.convertReceiptToRegularTransaction(receiptTransactionId)
            result.fold(
                onSuccess = { transactionId ->
                    Log.d(TAG, "Receipt transaction converted to regular transaction with ID: $transactionId")
                },
                onFailure = { exception ->
                    Log.e(TAG, "Failed to convert receipt to transaction: ${exception.message}", exception)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error converting receipt to transaction: ${e.message}", e)
        }
    }

    /**
     * Get the current OCR data if available
     */
    fun getCurrentOCRData(): com.example.financialplannerapp.data.model.ReceiptOCRData? {
        return when (val currentState = _state.value) {
            is ReceiptOCRState.Success -> currentState.data
            else -> null
        }
    }

    /**
     * Log current state for debugging
     */
    fun logCurrentState() {
        val currentState = when (val state = _state.value) {
            is ReceiptOCRState.Idle -> "Idle"
            is ReceiptOCRState.Processing -> "Processing"
            is ReceiptOCRState.Success -> "Success - ${state.data.items.size} items extracted"
            is ReceiptOCRState.Error -> "Error: ${state.message}"
            is ReceiptOCRState.Unauthenticated -> "Unauthenticated"
        }
        Log.d(TAG, "Current State: $currentState")
        Log.d(TAG, "User authenticated: ${tokenManager.isAuthenticated()}")
        Log.d(TAG, "Image URI: $currentImageUri")
    }

    private fun saveOcrResultToTransaction(ocrData: com.example.financialplannerapp.data.model.ReceiptOCRData) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: "local_user"
            val localReceiptItems = ocrData.items.map { item ->
                com.example.financialplannerapp.data.local.model.ReceiptItem(
                    name = item.name,
                    price = item.price,
                    quantity = item.quantity,
                    category = item.category ?: "Unknown"
                )
            }
            val transaction = com.example.financialplannerapp.data.local.model.TransactionEntity(
                userId = userId,
                amount = ocrData.totalAmount,
                type = if (ocrData.totalAmount >= 0) "INCOME" else "EXPENSE",
                date = try { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(ocrData.date) ?: java.util.Date() } catch (e: Exception) { java.util.Date() },
                pocket = "Cash",
                category = "General",
                note = "${ocrData.merchantName} - Receipt Transaction",
                isFromReceipt = true,
                receiptId = ocrData.receiptId ?: "receipt_${System.currentTimeMillis()}",
                merchantName = ocrData.merchantName,
                location = ocrData.location,
                receiptConfidence = ocrData.confidence,
                receipt_items = localReceiptItems,
                isSynced = false
            )
            transactionRepository.insertTransaction(transaction)
            Log.d(TAG, "Saved OCR transaction to RoomDB: $transaction")
        }
    }
}
