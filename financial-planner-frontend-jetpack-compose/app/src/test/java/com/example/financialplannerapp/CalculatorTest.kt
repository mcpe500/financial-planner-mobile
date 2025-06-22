package com.example.financialplannerapp

import org.junit.Test
import org.junit.Assert.*

class CalculatorTest {

    @Test
    fun calculateTotal_shouldSumAmounts() {
        val amounts = listOf(100.0, 200.0, 50.0)
        val total = calculateTotal(amounts)
        assertEquals(350.0, total, 0.01)
    }

    @Test
    fun calculateAverage_shouldReturnCorrectAverage() {
        val amounts = listOf(100.0, 200.0, 300.0)
        val average = calculateAverage(amounts)
        assertEquals(200.0, average, 0.01)
    }

    @Test
    fun calculatePercentage_shouldReturnCorrectPercentage() {
        val part = 25.0
        val whole = 100.0
        val percentage = calculatePercentage(part, whole)
        assertEquals(25.0, percentage, 0.01)
    }

    @Test
    fun calculateBudgetRemaining_shouldReturnCorrectAmount() {
        val budget = 1000.0
        val spent = 300.0
        val remaining = calculateBudgetRemaining(budget, spent)
        assertEquals(700.0, remaining, 0.01)
    }

    private fun calculateTotal(amounts: List<Double>): Double {
        return amounts.sum()
    }

    private fun calculateAverage(amounts: List<Double>): Double {
        return if (amounts.isEmpty()) 0.0 else amounts.sum() / amounts.size
    }

    private fun calculatePercentage(part: Double, whole: Double): Double {
        return if (whole == 0.0) 0.0 else (part / whole) * 100
    }

    private fun calculateBudgetRemaining(budget: Double, spent: Double): Double {
        return budget - spent
    }
}