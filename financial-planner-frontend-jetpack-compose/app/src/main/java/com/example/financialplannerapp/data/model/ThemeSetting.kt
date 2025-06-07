package com.example.financialplannerapp.data.model

enum class ThemeSetting(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");
    
    companion object {
        fun fromString(value: String): ThemeSetting {
            return values().find { it.value == value } ?: SYSTEM
        }
    }
}
