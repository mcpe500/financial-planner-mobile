package com.example.financialplannerapp.utils

import java.security.MessageDigest

/**
 * Security utility functions
 */
object SecurityUtils {
    
    /**
     * Hash PIN using SHA-256
     */
    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray())
        return hashBytes.fold("", { str, it -> str + "%02x".format(it) })
    }
}

/**
 * Extension function for easy access
 */
fun hashPin(pin: String): String = SecurityUtils.hashPin(pin)

/**
 * Verify PIN against stored hash
 */
fun verifyPin(pin: String, storedHash: String): Boolean {
    return hashPin(pin) == storedHash
}