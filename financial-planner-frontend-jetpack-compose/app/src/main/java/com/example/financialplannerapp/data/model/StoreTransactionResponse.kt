package com.example.financialplannerapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response model for storing transaction from OCR data
 */
@JsonClass(generateAdapter = true)
data class StoreTransactionResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: TransactionStoreData?
)

@JsonClass(generateAdapter = true)
data class TransactionStoreData(
    @Json(name = "transaction_id")
    val transaction_id: String,
    @Json(name = "amount")
    val amount: Double,
    @Json(name = "merchant")
    val merchant: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "receipt_id")
    val receipt_id: String
)
