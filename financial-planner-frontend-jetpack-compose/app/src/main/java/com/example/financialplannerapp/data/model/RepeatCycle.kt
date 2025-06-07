package com.example.financialplannerapp.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
enum class RepeatCycle(val label: String, val icon: String) : Parcelable {
    DAILY("Daily", "📅"),
    WEEKLY("Weekly", "📅"),
    MONTHLY("Monthly", "📅"),
    YEARLY("Yearly", "📅"),
    CUSTOM("Custom", "⚙️"); // Added a Custom option for more flexibility

    companion object {
        fun fromLabel(label: String): RepeatCycle? {
            return entries.find { it.label == label }
        }
    }
}
