package com.example.financialplannerapp.models.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financialplannerapp.models.api.User
import java.util.Date

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val userId: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val lastSynced: String? = null,
    val needsSync: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
){
    // Convert from API User model to UserProfile entity
    companion object {
        fun fromUser(user: User): UserProfile {
            return UserProfile(
                    userId = user.id,
                    name = user.name,
                    email = user.email,
                    phone = null, // User API doesn't have phone, set to null
                    avatarUrl = user.avatarUrl,
                    lastSynced = Date().toString(),
                    needsSync = false
            )
        }
    }

    // Convert to API User model for synchronization
    fun toUser(): User {
        return User(
                id = userId,
                email = email ?: "",
                name = name ?: "",
                avatarUrl = avatarUrl,
                googleId = null, // We don't modify this
                role = "user" // We don't modify roles from the profile page
        )
    }
}