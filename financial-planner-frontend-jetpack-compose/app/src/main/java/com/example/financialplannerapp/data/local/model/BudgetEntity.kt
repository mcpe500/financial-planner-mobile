package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "budgets",
    foreignKeys = [ForeignKey(
        entity = WalletEntity::class,
        parentColumns = ["id"],
        childColumns = ["walletId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val walletId: String,
    val userEmail: String,
    val name: String,
    val amount: Double,
    val category: String, // Consider relating this to a CategoryEntity if it exists
    val startDate: Date,
    val endDate: Date,
    val isRecurring: Boolean
) 