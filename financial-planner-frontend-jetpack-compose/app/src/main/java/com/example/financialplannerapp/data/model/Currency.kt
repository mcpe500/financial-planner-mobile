package com.example.financialplannerapp.data.model

enum class Currency(val code: String, val symbol: String, val displayName: String) {
    IDR("IDR", "Rp", "Indonesian Rupiah"),
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    JPY("JPY", "¥", "Japanese Yen");
    
    companion object {
        fun fromCode(code: String): Currency? {
            return values().find { it.code == code }
        }
    }
}
