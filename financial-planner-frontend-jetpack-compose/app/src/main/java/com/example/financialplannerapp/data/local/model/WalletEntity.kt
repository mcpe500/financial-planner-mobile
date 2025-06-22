package com.example.financialplannerapp.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID


@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey // Using String for ID allows UUIDs or custom IDs
    val id: String, // Keep id as String to match your UI Wallet data class

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String, // Store WalletType as String (e.g., "CASH", "BANK")

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "color_hex") // Store color as a hex string (e.g., "#4CAF50")
    val colorHex: String,

    // Add other fields relevant to your wallet (e.g., userId if wallets are per-user)
    @ColumnInfo(name = "user_email")
    val userEmail: String, 

    @ColumnInfo(name = "icon_name")
    val iconName: String 
)