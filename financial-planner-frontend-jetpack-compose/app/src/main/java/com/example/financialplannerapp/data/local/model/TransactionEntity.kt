package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val date: Date,
    val pocket: String,
    val category: String,
    val note: String? = null,
    val tags: List<String>? = null,
    // Untuk hasil scan
    val isFromReceipt: Boolean = false,
    val receiptId: String? = null,
    val merchantName: String? = null,
    val location: String? = null,
    val receiptImagePath: String? = null,
    val receiptConfidence: Double? = null,
    // Sync
    val isSynced: Boolean = false,
    val backendTransactionId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)