package com.example.financialplannerapp.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String,
    val name: String,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("google_id") val googleId: String? = null,
    val role: String
)