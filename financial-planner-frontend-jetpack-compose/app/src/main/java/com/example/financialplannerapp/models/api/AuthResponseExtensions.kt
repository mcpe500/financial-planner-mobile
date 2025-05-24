package com.example.financialplannerapp.models.api

fun AuthResponse.toUserData(): UserData {
    return UserData(
        id = this.userId,
        name = this.userName,
        email = this.userEmail,
        profileImageUrl = this.profileImageUrl
    )
}