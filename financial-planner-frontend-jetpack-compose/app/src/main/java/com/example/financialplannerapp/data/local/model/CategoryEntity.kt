package com.example.financialplannerapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CategoryEntity - Entity class for categories in Room database
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val colorCode: String? = null,
    val iconName: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
