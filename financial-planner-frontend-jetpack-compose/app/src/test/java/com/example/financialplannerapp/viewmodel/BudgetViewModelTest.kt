package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class BudgetViewModelTest {

    @Test
    fun calculateBudgetProgress_shouldReturnCorrectPercentage() {
        val budget = 1000.0
        val spent = 300.0
        val progress = calculateBudgetProgress(spent, budget)
        assertEquals(30.0, progress, 0.01)
    }

    @Test
    fun calculateRemainingBudget_shouldReturnCorrectAmount() {
        val budget = 1000.0
        val spent = 300.0
        val remaining = calculateRemainingBudget(budget, spent)
        assertEquals(700.0, remaining, 0.01)
    }

    @Test
    fun isBudgetExceeded_shouldReturnTrueWhenOverBudget() {
        val budget = 1000.0
        val spent = 1200.0
        assertTrue(isBudgetExceeded(spent, budget))
    }

    @Test
    fun isBudgetExceeded_shouldReturnFalseWhenUnderBudget() {
        val budget = 1000.0
        val spent = 800.0
        assertFalse(isBudgetExceeded(spent, budget))
    }

    @Test
    fun validateBudgetAmount_shouldReturnTrueForPositiveAmount() {
        val amount = 500.0
        assertTrue(isValidBudgetAmount(amount))
    }

    private fun calculateBudgetProgress(spent: Double, budget: Double): Double {
        return if (budget > 0) (spent / budget) * 100 else 0.0
    }

    private fun calculateRemainingBudget(budget: Double, spent: Double): Double {
        return budget - spent
    }

    private fun isBudgetExceeded(spent: Double, budget: Double): Boolean {
        return spent > budget
    }

    private fun isValidBudgetAmount(amount: Double): Boolean {
        return amount > 0
    }
}