package com.example.financialplannerapp.models.api

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String?, // Token might not always be present (e.g. in verify)
    @SerializedName("user") val user: UserData?
)

data class UserData(
    @SerializedName("_id") val id: String,
    @SerializedName("googleId") val googleId: String?,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String?,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("createdAt") val createdAt: String, // Dates as String, parse as needed
    @SerializedName("updatedAt") val updatedAt: String
)