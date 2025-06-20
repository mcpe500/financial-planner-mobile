package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * ReceiptTransactionEntity - Entity class for receipt-based transactions in Room database
 * This stores transactions created from OCR receipt processing
 */
@Entity(tableName = "receipt_transactions")
data class ReceiptTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val receiptId: String,
    val totalAmount: Double,
    val merchantName: String,
    val date: Date,
    val location: String? = null,
    val confidence: Double = 0.0,
    val items: String? = null, // JSON string of receipt items
    val category: String? = null,
    val notes: String? = null,
    val userId: String,
    val isProcessed: Boolean = false, // Whether this has been converted to a regular transaction
    val isSynced: Boolean = false, // Whether this has been synced to backend
    val backendTransactionId: String? = null, // ID from backend after sync
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Helper data class for receipt items
 */
data class ReceiptItemData(
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val category: String? = null
)
