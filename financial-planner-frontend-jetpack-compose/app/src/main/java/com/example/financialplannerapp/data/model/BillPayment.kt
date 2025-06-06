package com.example.financialplannerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class BillPayment(
    val id: String = "",
    val amount: Double = 0.0,
    val date: Date = Date(),
    val notes: String = ""
) : Parcelable
