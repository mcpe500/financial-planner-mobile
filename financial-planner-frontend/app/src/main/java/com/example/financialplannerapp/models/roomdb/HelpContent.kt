package com.example.financialplannerapp.models.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "help_content")
data class HelpContent(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String, // "faq", "guide", "contact"
    val order: Int = 0,
    val isSearchable: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false
)

@Entity(tableName = "faq_items")
data class FAQItem(
    @PrimaryKey
    val id: String,
    val question: String,
    val answer: String,
    val category: String = "general",
    val order: Int = 0,
    val isPopular: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false
)