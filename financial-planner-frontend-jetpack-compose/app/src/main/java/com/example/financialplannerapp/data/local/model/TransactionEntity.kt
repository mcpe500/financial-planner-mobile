package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * TransactionEntity - Entity class for transactions in Room database
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val date: Date,
    val description: String,
    val categoryId: Int? = null,
    val type: String, // "income" or "expense"
    val userId: String,
    val accountId: String? = null,
    val tags: String? = null, // JSON string of tags
    val location: String? = null,
    val receiptImagePath: String? = null,
    val isRecurring: Boolean = false,
    val recurringType: String? = null, // "daily", "weekly", "monthly", "yearly"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
