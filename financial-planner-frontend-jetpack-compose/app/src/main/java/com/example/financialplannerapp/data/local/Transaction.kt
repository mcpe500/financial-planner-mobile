package com.example.financialplannerapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: Date,
    val description: String?,
    val merchantName: String?,
    val location: String?,
    val receiptId: String?,
    val items: String?,
    val notes: String?,
    val tags: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val syncStatus: String
)