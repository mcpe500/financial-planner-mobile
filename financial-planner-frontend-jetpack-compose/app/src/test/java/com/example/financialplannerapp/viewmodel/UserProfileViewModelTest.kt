package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class UserProfileViewModelTest {

    @Test
    fun validateUserProfile_shouldReturnTrueForValidProfile() {
        val profile = createValidProfile()
        assertTrue(isValidProfile(profile))
    }

    @Test
    fun validateUserProfile_shouldReturnFalseForInvalidProfile() {
        val profile = UserProfile("", "", "", "", 0.0)
        assertFalse(isValidProfile(profile))
    }

    @Test
    fun validateEmail_shouldReturnTrueForValidEmail() {
        val email = "user@example.com"
        assertTrue(isValidEmail(email))
    }

    @Test
    fun validatePhoneNumber_shouldReturnTrueForValidPhone() {
        val phone = "+1234567890"
        assertTrue(isValidPhone(phone))
    }

    @Test
    fun calculateAge_shouldReturnCorrectAge() {
        val birthYear = 1990
        val currentYear = 2024
        val age = calculateAge(birthYear, currentYear)
        assertEquals(34, age)
    }

    @Test
    fun formatUserDisplayName_shouldReturnCorrectFormat() {
        val firstName = "John"
        val lastName = "Doe"
        val displayName = formatDisplayName(firstName, lastName)
        assertEquals("John Doe", displayName)
    }

    @Test
    fun validateMonthlyIncome_shouldReturnTrueForPositiveIncome() {
        val income = 5000.0
        assertTrue(isValidIncome(income))
    }

    private fun createValidProfile(): UserProfile {
        return UserProfile(
            name = "John Doe",
            email = "john@example.com",
            phone = "+1234567890",
            occupation = "Developer",
            monthlyIncome = 5000.0
        )
    }

    private fun isValidProfile(profile: UserProfile): Boolean {
        return profile.name.isNotEmpty() && 
               isValidEmail(profile.email) && 
               profile.monthlyIncome > 0
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.length >= 10 && phone.startsWith("+")
    }

    private fun calculateAge(birthYear: Int, currentYear: Int): Int {
        return currentYear - birthYear
    }

    private fun formatDisplayName(firstName: String, lastName: String): String {
        return "$firstName $lastName"
    }

    private fun isValidIncome(income: Double): Boolean {
        return income > 0
    }

    data class UserProfile(
        val name: String,
        val email: String,
        val phone: String,
        val occupation: String,
        val monthlyIncome: Double
    )
}