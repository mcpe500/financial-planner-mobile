package com.example.financialplannerapp.models.api

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null,
    val googleId: String? = null,
    val role: String = "user"
)