package com.example.financialplannerapp.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.util.Date

data class Budget(
    val id: Int = 0,
    val walletId: String,
    val userEmail: String,
    val name: String,
    val amount: Double,
    val category: String,
    val startDate: Date,
    val endDate: Date,
    val isRecurring: Boolean,
    val spentAmount: Double = 0.0,
    val status: BudgetStatus = BudgetStatus.ON_TRACK
) {
    val progressPercentage: Float
        get() = (spentAmount / amount).toFloat().coerceIn(0f, 1f)
    
    val remainingAmount: Double
        get() = (amount - spentAmount).coerceAtLeast(0.0)
    
    val categoryIcon: ImageVector
        get() = when (category.lowercase()) {
            "food", "makanan" -> Icons.Default.Restaurant
            "transport", "transportasi" -> Icons.Default.DirectionsCar
            "entertainment", "hiburan" -> Icons.Default.Movie
            "shopping", "belanja" -> Icons.Default.ShoppingCart
            "health", "kesehatan" -> Icons.Default.LocalHospital
            "education", "pendidikan" -> Icons.Default.School
            "utilities", "utilitas" -> Icons.Default.Power
            "housing", "perumahan" -> Icons.Default.Home
            else -> Icons.Default.Category
        }
}

enum class BudgetStatus {
    ON_TRACK,
    WARNING,
    EXCEEDED
}

fun generateMockBudgets(): List<Budget> {
    return listOf(
        Budget(
            id = 1,
            walletId = "wallet1",
            userEmail = "user@example.com",
            name = "Makanan Bulanan",
            amount = 2000000.0,
            category = "Food",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            isRecurring = true,
            spentAmount = 1500000.0
        ),
        Budget(
            id = 2,
            walletId = "wallet1",
            userEmail = "user@example.com",
            name = "Transportasi",
            amount = 500000.0,
            category = "Transport",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            isRecurring = true,
            spentAmount = 450000.0
        ),
        Budget(
            id = 3,
            walletId = "wallet2",
            userEmail = "user@example.com",
            name = "Hiburan",
            amount = 300000.0,
            category = "Entertainment",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            isRecurring = true,
            spentAmount = 100000.0
        )
    )
} 