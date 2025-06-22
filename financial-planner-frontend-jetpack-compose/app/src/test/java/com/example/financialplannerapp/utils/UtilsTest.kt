package com.example.financialplannerapp.utils

import org.junit.Test
import org.junit.Assert.*

class UtilsTest {

    @Test
    fun formatCurrency_shouldFormatCorrectly() {
        val amount = 1000.0
        val formatted = formatCurrency(amount)
        assertEquals("$1,000.00", formatted)
    }

    @Test
    fun formatCurrency_shouldHandleZero() {
        val amount = 0.0
        val formatted = formatCurrency(amount)
        assertEquals("$0.00", formatted)
    }

    private fun formatCurrency(amount: Double): String {
        return "$${String.format("%,.2f", amount)}"
    }
}