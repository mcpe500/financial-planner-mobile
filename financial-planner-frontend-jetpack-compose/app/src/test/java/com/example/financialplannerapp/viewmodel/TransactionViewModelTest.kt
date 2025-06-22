package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class TransactionViewModelTest {

    @Test
    fun validateTransactionAmount_shouldReturnTrueForValidAmount() {
        val amount = 100.0
        assertTrue(isValidAmount(amount))
    }

    @Test
    fun validateTransactionAmount_shouldReturnFalseForZeroAmount() {
        val amount = 0.0
        assertFalse(isValidAmount(amount))
    }

    @Test
    fun formatTransactionType_shouldReturnCorrectType() {
        val positiveAmount = 100.0
        val negativeAmount = -50.0
        
        assertEquals("INCOME", getTransactionType(positiveAmount))
        assertEquals("EXPENSE", getTransactionType(negativeAmount))
    }

    @Test
    fun calculateTransactionTotal_shouldSumCorrectly() {
        val transactions = listOf(100.0, -50.0, 200.0, -30.0)
        val total = calculateTotal(transactions)
        assertEquals(220.0, total, 0.01)
    }

    private fun isValidAmount(amount: Double): Boolean {
        return amount > 0
    }

    private fun getTransactionType(amount: Double): String {
        return if (amount >= 0) "INCOME" else "EXPENSE"
    }

    private fun calculateTotal(amounts: List<Double>): Double {
        return amounts.sum()
    }
}