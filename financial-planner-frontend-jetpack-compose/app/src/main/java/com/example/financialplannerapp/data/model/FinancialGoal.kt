package com.example.financialplannerapp.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.util.Date

data class FinancialGoal(
    val id: Int = 0,
    val walletId: String,
    val userEmail: String,
    val name: String,
    val targetAmount: Double,
    var currentAmount: Double,
    val targetDate: Date,
    val priority: String,
    val category: GoalCategory = GoalCategory.SAVINGS,
    val description: String = "",
    val icon: ImageVector = Icons.Default.Savings,
    val createdDate: Date = Date()
) {
    val progressPercentage: Float
        get() = (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)
    
    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
    
    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
    
    val daysRemaining: Int
        get() = ((targetDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
    
    val estimatedCompletionDate: Date
        get() = if (currentAmount > 0) {
            val dailySaving = currentAmount / ((Date().time - createdDate.time) / (1000 * 60 * 60 * 24)).toDouble().coerceAtLeast(1.0)
            val remainingDays = remainingAmount / dailySaving.coerceAtLeast(1.0)
            Date(System.currentTimeMillis() + (remainingDays * 24 * 60 * 60 * 1000).toLong())
        } else {
            targetDate
        }
}

enum class GoalCategory(val displayName: String, val icon: ImageVector) {
    SAVINGS("Tabungan", Icons.Default.Savings),
    INVESTMENT("Investasi", Icons.Default.TrendingUp),
    EMERGENCY_FUND("Dana Darurat", Icons.Default.Emergency),
    VACATION("Liburan", Icons.Default.Flight),
    EDUCATION("Pendidikan", Icons.Default.School),
    HOME("Rumah", Icons.Default.Home),
    VEHICLE("Kendaraan", Icons.Default.DirectionsCar),
    WEDDING("Pernikahan", Icons.Default.Favorite),
    BUSINESS("Bisnis", Icons.Default.Business),
    RETIREMENT("Pensiun", Icons.Default.Person)
}

fun generateMockGoals(): List<FinancialGoal> {
    return listOf(
        FinancialGoal(
            id = 1,
            walletId = "wallet1",
            userEmail = "user@example.com",
            name = "Tabungan Liburan",
            targetAmount = 10000000.0,
            currentAmount = 3000000.0,
            targetDate = Date(System.currentTimeMillis() + 180L * 24 * 60 * 60 * 1000),
            priority = "Medium",
            category = GoalCategory.VACATION,
            icon = Icons.Default.Flight
        ),
        FinancialGoal(
            id = 2,
            walletId = "wallet1",
            userEmail = "user@example.com",
            name = "Dana Darurat",
            targetAmount = 50000000.0,
            currentAmount = 25000000.0,
            targetDate = Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000),
            priority = "High",
            category = GoalCategory.EMERGENCY_FUND,
            icon = Icons.Default.Emergency
        ),
        FinancialGoal(
            id = 3,
            walletId = "wallet2",
            userEmail = "user@example.com",
            name = "Investasi Saham",
            targetAmount = 20000000.0,
            currentAmount = 5000000.0,
            targetDate = Date(System.currentTimeMillis() + 730L * 24 * 60 * 60 * 1000),
            priority = "Medium",
            category = GoalCategory.INVESTMENT,
            icon = Icons.Default.TrendingUp
        )
    )
} 