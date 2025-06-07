package com.example.financialplannerapp.data.model

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Models for Receipt OCR functionality
 */

// Request models
@JsonClass(generateAdapter = true)
data class ReceiptOCRRequest(
    @Json(name = "image_base64")
    val imageBase64: String,
    @Json(name = "user_id")
    val userId: String
)

// Response models
@JsonClass(generateAdapter = true)
data class ReceiptOCRResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "message")
    val message: String?,
    @Json(name = "data")
    val data: ReceiptOCRData
)

@JsonClass(generateAdapter = true)
data class ReceiptOCRData(
    @Json(name = "total_amount")
    val totalAmount: Double,
    @Json(name = "merchant_name")
    val merchantName: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "items")
    val items: List<ReceiptItem>,
    @Json(name = "location")
    val location: String? = null,
    @Json(name = "confidence")
    val confidence: Double = 0.0,
    @Json(name = "receipt_id")
    val receiptId: String? = null
)

@JsonClass(generateAdapter = true)
data class ReceiptItem(
    @Json(name = "name")
    val name: String,
    @Json(name = "price")
    val price: Double,
    @Json(name = "quantity")
    val quantity: Int = 1,
    @Json(name = "category")
    val category: String? = null,
    @Json(name = "confidence")
    val confidence: Double? = null
)

// UI State sealed class
sealed class ReceiptOCRState {
    object Idle : ReceiptOCRState()
    object Processing : ReceiptOCRState()
    object Unauthenticated : ReceiptOCRState()
    data class Success(val data: ReceiptOCRData) : ReceiptOCRState()
    data class Error(val message: String) : ReceiptOCRState()
}

// Transaction creation model from OCR
@JsonClass(generateAdapter = true)
data class TransactionFromOCR(
    val amount: Double,
    val merchant: String,
    val date: String,
    val items: List<ReceiptItem>,
    val receiptId: String
)
