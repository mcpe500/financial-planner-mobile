package com.example.financialplannerapp.data

/**
 * User Profile Data Class
 * 
 * Simple data class for user profile data used throughout the data layer.
 */
data class UserProfileData(
    val userId: String,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val occupation: String = "",
    val monthlyIncome: String = "",
    val financialGoals: String = "",
    val lastSyncTime: String = "",
    val needsSync: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Legacy UserProfileData class for compatibility with existing code
 */
data class LegacyUserProfileData(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val occupation: String = "",
    val monthlyIncome: String = "",
    val financialGoals: String = "",
    val lastSyncTime: String = "",
    val isDataModified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)