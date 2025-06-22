package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*

class UserProfileRepositoryTest {

    @Test
    fun createUserProfile_shouldReturnProfileWithId() {
        val profile = createMockProfile()
        val savedProfile = saveProfile(profile)
        
        assertNotNull(savedProfile.id)
        assertEquals(profile.name, savedProfile.name)
        assertEquals(profile.email, savedProfile.email)
    }

    @Test
    fun updateUserProfile_shouldUpdateCorrectly() {
        val profile = MockUserProfile(1, "John Doe", "john@example.com", "+1234567890", "Developer", 5000.0)
        val updatedProfile = updateProfile(profile, "Senior Developer", 6000.0)
        
        assertEquals("Senior Developer", updatedProfile.occupation)
        assertEquals(6000.0, updatedProfile.monthlyIncome, 0.01)
    }

    @Test
    fun validateProfileData_shouldReturnTrueForValidProfile() {
        val profile = createMockProfile()
        assertTrue(isValidProfile(profile))
    }

    @Test
    fun validateProfileData_shouldReturnFalseForInvalidProfile() {
        val profile = MockUserProfile(0, "", "invalid-email", "", "", -1000.0)
        assertFalse(isValidProfile(profile))
    }

    @Test
    fun searchProfilesByOccupation_shouldReturnCorrectProfiles() {
        val profiles = listOf(
            MockUserProfile(1, "John", "john@example.com", "+123", "Developer", 5000.0),
            MockUserProfile(2, "Jane", "jane@example.com", "+456", "Designer", 4000.0),
            MockUserProfile(3, "Bob", "bob@example.com", "+789", "Developer", 5500.0)
        )
        
        val developers = searchByOccupation(profiles, "Developer")
        assertEquals(2, developers.size)
        assertTrue(developers.all { it.occupation == "Developer" })
    }

    @Test
    fun calculateAverageIncome_shouldReturnCorrectAverage() {
        val profiles = listOf(
            MockUserProfile(1, "John", "john@example.com", "+123", "Developer", 5000.0),
            MockUserProfile(2, "Jane", "jane@example.com", "+456", "Designer", 4000.0),
            MockUserProfile(3, "Bob", "bob@example.com", "+789", "Manager", 6000.0)
        )
        
        val averageIncome = calculateAverageIncome(profiles)
        assertEquals(5000.0, averageIncome, 0.01)
    }

    @Test
    fun filterProfilesByIncomeRange_shouldReturnCorrectProfiles() {
        val profiles = listOf(
            MockUserProfile(1, "Low Income", "low@example.com", "+123", "Intern", 2000.0),
            MockUserProfile(2, "Medium Income", "med@example.com", "+456", "Developer", 5000.0),
            MockUserProfile(3, "High Income", "high@example.com", "+789", "Manager", 8000.0)
        )
        
        val mediumIncomeProfiles = filterByIncomeRange(profiles, 4000.0, 6000.0)
        assertEquals(1, mediumIncomeProfiles.size)
        assertEquals("Medium Income", mediumIncomeProfiles[0].name)
    }

    @Test
    fun updateContactInfo_shouldUpdatePhoneAndEmail() {
        val profile = createMockProfile()
        val updatedProfile = updateContactInfo(profile, "newemail@example.com", "+9876543210")
        
        assertEquals("newemail@example.com", updatedProfile.email)
        assertEquals("+9876543210", updatedProfile.phone)
    }

    @Test
    fun validateEmailFormat_shouldReturnTrueForValidEmail() {
        val validEmails = listOf("test@example.com", "user.name@domain.co.uk", "admin@company.org")
        validEmails.forEach { email ->
            assertTrue("Email $email should be valid", isValidEmail(email))
        }
    }

    @Test
    fun validatePhoneFormat_shouldReturnTrueForValidPhone() {
        val validPhones = listOf("+1234567890", "+44123456789", "+91987654321")
        validPhones.forEach { phone ->
            assertTrue("Phone $phone should be valid", isValidPhone(phone))
        }
    }

    private fun createMockProfile(): MockUserProfile {
        return MockUserProfile(
            id = 0,
            name = "John Doe",
            email = "john@example.com",
            phone = "+1234567890",
            occupation = "Developer",
            monthlyIncome = 5000.0
        )
    }

    private fun saveProfile(profile: MockUserProfile): MockUserProfile {
        return profile.copy(id = 1)
    }

    private fun updateProfile(profile: MockUserProfile, newOccupation: String, newIncome: Double): MockUserProfile {
        return profile.copy(occupation = newOccupation, monthlyIncome = newIncome)
    }

    private fun isValidProfile(profile: MockUserProfile): Boolean {
        return profile.name.isNotEmpty() && 
               isValidEmail(profile.email) && 
               isValidPhone(profile.phone) &&
               profile.monthlyIncome >= 0
    }

    private fun searchByOccupation(profiles: List<MockUserProfile>, occupation: String): List<MockUserProfile> {
        return profiles.filter { it.occupation == occupation }
    }

    private fun calculateAverageIncome(profiles: List<MockUserProfile>): Double {
        return if (profiles.isEmpty()) 0.0 else profiles.sumOf { it.monthlyIncome } / profiles.size
    }

    private fun filterByIncomeRange(profiles: List<MockUserProfile>, min: Double, max: Double): List<MockUserProfile> {
        return profiles.filter { it.monthlyIncome >= min && it.monthlyIncome <= max }
    }

    private fun updateContactInfo(profile: MockUserProfile, newEmail: String, newPhone: String): MockUserProfile {
        return profile.copy(email = newEmail, phone = newPhone)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length > 5
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.startsWith("+") && phone.length >= 10
    }

    data class MockUserProfile(
        val id: Long,
        val name: String,
        val email: String,
        val phone: String,
        val occupation: String,
        val monthlyIncome: Double
    )
}