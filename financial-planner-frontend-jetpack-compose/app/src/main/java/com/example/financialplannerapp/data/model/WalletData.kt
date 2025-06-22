package com.example.financialplannerapp.data.model

import com.example.financialplannerapp.data.local.model.WalletEntity

data class WalletData(
    val id: String,
    val name: String,
    val type: String,
    val balance: Double,
    val colorHex: String,
    val iconName: String,
    val userEmail: String
)

fun WalletData.toEntity(): WalletEntity {
    return WalletEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        balance = this.balance,
        colorHex = this.colorHex,
        iconName = this.iconName,
        userEmail = this.userEmail
    )
}

fun WalletEntity.toWalletData(): WalletData {
    return WalletData(
        id = this.id,
        name = this.name,
        type = this.type,
        balance = this.balance,
        colorHex = this.colorHex,
        iconName = this.iconName,
        userEmail = this.userEmail
    )
}