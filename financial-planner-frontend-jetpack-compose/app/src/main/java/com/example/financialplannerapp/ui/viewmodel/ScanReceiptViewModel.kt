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
    private val tokenManager: TokenManager
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
        if (!tokenManager.isAuthenticated()) {
            _state.value = ReceiptOCRState.Unauthenticated
            Log.w(TAG, "Attempted to process receipt without authentication")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = ReceiptOCRState.Processing
                Log.d(TAG, "Starting receipt processing for image: $imageUri")

                val result = receiptService.processReceiptOCR(imageUri)

                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "OCR processing successful. Found ${response.data.items.size} items")
                        _state.value = ReceiptOCRState.Success(response.data)

                        // Log extracted data for debugging
                        Log.d(TAG, "Merchant: ${response.data.merchantName}")
                        Log.d(TAG, "Total: $${response.data.totalAmount}")
                        Log.d(TAG, "Items: ${response.data.items.size}")
                        response.data.items.forEach { item ->
                            Log.d(TAG, "  - ${item.name}: $${item.price}")
                        }
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
    }    /**
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
}
