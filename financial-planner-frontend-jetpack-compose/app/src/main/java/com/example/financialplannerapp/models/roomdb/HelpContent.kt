package com.example.financialplannerapp.models.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "help_content")
data class HelpContent(
    @PrimaryKey val id: String,
    val title: String,
    val content: String, // Can be long text, potentially HTML or Markdown
    val category: String, // e.g., "guide", "tutorial", "troubleshooting"
    val order: Int = 0, // For ordering within a category
    val lastUpdated: Long = System.currentTimeMillis() // Timestamp for sync
)