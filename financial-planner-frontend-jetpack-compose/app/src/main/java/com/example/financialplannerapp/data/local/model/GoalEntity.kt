package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "goals",
    foreignKeys = [ForeignKey(
        entity = WalletEntity::class,
        parentColumns = ["id"],
        childColumns = ["walletId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val walletId: String,
    val user_email: String,
    val name: String,
    val targetAmount: Double,
    var currentAmount: Double,
    val targetDate: Date,
    val priority: String // e.g., "High", "Medium", "Low"
)
