package com.example.financialplannerapp.models.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "faq_items")
data class FAQItem(
    @PrimaryKey val id: String,
    val question: String,
    val answer: String,
    val category: String, // e.g., "general", "transactions", "reports"
    val order: Int = 0, // For ordering within a category
    val isPopular: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis() // Timestamp for sync
)