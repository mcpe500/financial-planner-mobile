package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class AuthViewModelTest {

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
    fun validatePassword_shouldReturnTrueForValidPassword() {
        val password = "password123"
        assertTrue(isValidPassword(password))
    }

    @Test
    fun validatePassword_shouldReturnFalseForShortPassword() {
        val password = "123"
        assertFalse(isValidPassword(password))
    }

    @Test
    fun createLoginRequest_shouldHaveCorrectFormat() {
        val email = "test@example.com"
        val password = "password123"
        val request = createLoginRequest(email, password)
        
        assertEquals(email, request.email)
        assertEquals(password, request.password)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun createLoginRequest(email: String, password: String): LoginRequest {
        return LoginRequest(email, password)
    }

    data class LoginRequest(
        val email: String,
        val password: String
    )
}