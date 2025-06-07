package com.example.financialplannerapp.data.model

enum class BillFilter(val label: String) {
    ALL("Semua"),
    UPCOMING("Mendatang"),
    PAID("Lunas"),
    UNPAID("Belum Bayar")
}
