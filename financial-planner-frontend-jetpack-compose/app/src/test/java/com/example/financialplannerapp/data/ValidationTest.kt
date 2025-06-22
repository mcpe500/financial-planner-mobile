package com.example.financialplannerapp.data

import org.junit.Test
import org.junit.Assert.*

class ValidationTest {

    @Test
    fun validateEmail_shouldReturnTrueForValidEmail() {
        val email = "test@example.com"
        assertTrue(isValidEmail(email))
    }

    @Test
    fun validateEmail_shouldReturnFalseForInvalidEmail() {
        val email = "invalid-email"
        assertFalse(isValidEmail(email))
    }

    @Test
    fun validateAmount_shouldReturnTrueForPositiveAmount() {
        val amount = 100.0
        assertTrue(isValidAmount(amount))
    }

    @Test
    fun validateAmount_shouldReturnFalseForNegativeAmount() {
        val amount = -50.0
        assertFalse(isValidAmount(amount))
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidAmount(amount: Double): Boolean {
        return amount > 0
    }
}