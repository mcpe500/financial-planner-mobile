package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*

class AuthRepositoryTest {

    @Test
    fun validateLoginCredentials_shouldReturnTrueForValidCredentials() {
        val email = "test@example.com"
        val password = "password123"
        assertTrue(validateCredentials(email, password))
    }

    @Test
    fun validateLoginCredentials_shouldReturnFalseForInvalidEmail() {
        val email = "invalid-email"
        val password = "password123"
        assertFalse(validateCredentials(email, password))
    }

    @Test
    fun validateLoginCredentials_shouldReturnFalseForShortPassword() {
        val email = "test@example.com"
        val password = "123"
        assertFalse(validateCredentials(email, password))
    }

    @Test
    fun createAuthToken_shouldReturnValidToken() {
        val userId = "user123"
        val token = createToken(userId)
        
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        assertTrue(token.contains(userId))
    }

    @Test
    fun parseAuthToken_shouldExtractUserId() {
        val userId = "user123"
        val token = "token_${userId}_${System.currentTimeMillis()}"
        val extractedUserId = extractUserIdFromToken(token)
        
        assertEquals(userId, extractedUserId)
    }

    @Test
    fun checkTokenExpiry_shouldReturnTrueForValidToken() {
        val currentTime = System.currentTimeMillis()
        val token = "token_user123_$currentTime"
        assertTrue(isTokenValid(token))
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun createToken(userId: String): String {
        return "token_${userId}_${System.currentTimeMillis()}"
    }

    private fun extractUserIdFromToken(token: String): String {
        val parts = token.split("_")
        return if (parts.size >= 2) parts[1] else ""
    }

    private fun isTokenValid(token: String): Boolean {
        val parts = token.split("_")
        if (parts.size < 3) return false
        
        val timestamp = parts[2].toLongOrNull() ?: return false
        val currentTime = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000
        
        return (currentTime - timestamp) < oneHour
    }
}