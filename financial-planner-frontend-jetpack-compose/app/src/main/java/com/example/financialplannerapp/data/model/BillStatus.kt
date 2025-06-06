package com.example.financialplannerapp.data.model

import androidx.compose.ui.graphics.Color

enum class BillStatus(val label: String, val color: Color) {
    PAID("Lunas", Color(0xFF38A169)),
    OVERDUE("Terlambat", Color(0xFFE53E3E)),
    DUE_SOON("Jatuh Tempo", Color(0xFFFF9800)),
    UPCOMING("Mendatang", Color(0xFF2196F3))
}
