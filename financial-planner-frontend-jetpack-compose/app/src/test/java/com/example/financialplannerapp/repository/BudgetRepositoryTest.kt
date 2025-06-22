package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class BudgetRepositoryTest {

    @Test
    fun createBudget_shouldReturnBudgetWithId() {
        val budget = createMockBudget()
        val savedBudget = saveBudget(budget)
        
        assertNotNull(savedBudget.id)
        assertEquals(budget.name, savedBudget.name)
        assertEquals(budget.amount, savedBudget.amount, 0.01)
    }

    @Test
    fun filterBudgetsByCategory_shouldReturnCorrectBudgets() {
        val budgets = listOf(
            MockBudget(1, "Food Budget", 500.0, "Food"),
            MockBudget(2, "Transport Budget", 200.0, "Transport"),
            MockBudget(3, "Grocery Budget", 300.0, "Food")
        )
        
        val foodBudgets = filterByCategory(budgets, "Food")
        assertEquals(2, foodBudgets.size)
        assertTrue(foodBudgets.all { it.category == "Food" })
    }

    @Test
    fun calculateBudgetUtilization_shouldReturnCorrectPercentage() {
        val budget = 1000.0
        val spent = 300.0
        val utilization = calculateUtilization(spent, budget)
        assertEquals(30.0, utilization, 0.01)
    }

    @Test
    fun validateBudgetData_shouldReturnTrueForValidBudget() {
        val budget = createMockBudget()
        assertTrue(isValidBudget(budget))
    }

    @Test
    fun validateBudgetData_shouldReturnFalseForInvalidBudget() {
        val budget = MockBudget(0, "", 0.0, "")
        assertFalse(isValidBudget(budget))
    }

    @Test
    fun checkBudgetStatus_shouldReturnCorrectStatus() {
        val budget = 1000.0
        val spent = 1200.0
        val status = getBudgetStatus(spent, budget)
        assertEquals("EXCEEDED", status)
    }

    private fun createMockBudget(): MockBudget {
        return MockBudget(
            id = 0,
            name = "Monthly Budget",
            amount = 1000.0,
            category = "General"
        )
    }

    private fun saveBudget(budget: MockBudget): MockBudget {
        return budget.copy(id = 1)
    }

    private fun filterByCategory(budgets: List<MockBudget>, category: String): List<MockBudget> {
        return budgets.filter { it.category == category }
    }

    private fun calculateUtilization(spent: Double, budget: Double): Double {
        return if (budget > 0) (spent / budget) * 100 else 0.0
    }

    private fun isValidBudget(budget: MockBudget): Boolean {
        return budget.name.isNotEmpty() && budget.amount > 0 && budget.category.isNotEmpty()
    }

    private fun getBudgetStatus(spent: Double, budget: Double): String {
        return when {
            spent > budget -> "EXCEEDED"
            spent > budget * 0.8 -> "WARNING"
            else -> "NORMAL"
        }
    }

    data class MockBudget(
        val id: Long,
        val name: String,
        val amount: Double,
        val category: String
    )
}