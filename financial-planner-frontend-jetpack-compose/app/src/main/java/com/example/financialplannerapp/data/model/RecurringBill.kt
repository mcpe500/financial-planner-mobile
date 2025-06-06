package com.example.financialplannerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.Calendar

@Parcelize
data class RecurringBill(
    val id: String = "",
    val name: String = "",
    val estimatedAmount: Double = 0.0,
    val dueDate: Date = Date(),
    val repeatCycle: RepeatCycle = RepeatCycle.MONTHLY,
    val notes: String = "",
    val isReminderEnabled: Boolean = true,
    val reminderTime: String = "09:00",
    val reminderDaysBefore: Int = 1,
    val payments: List<BillPayment> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val nextDueDate: Date get() = calculateNextDueDate()
    val isOverdue: Boolean get() = Date().after(nextDueDate) && !isRecentlyPaid()
    val daysToDue: Int get() = ((nextDueDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
    val status: BillStatus get() = when {
        isRecentlyPaid() -> BillStatus.PAID
        isOverdue -> BillStatus.OVERDUE
        daysToDue <= 7 -> BillStatus.DUE_SOON
        else -> BillStatus.UPCOMING
    }

    private fun calculateNextDueDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = dueDate
        when (repeatCycle) {
            RepeatCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RepeatCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RepeatCycle.QUARTERLY -> calendar.add(Calendar.MONTH, 3)
            RepeatCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
            RepeatCycle.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.time
    }

    private fun isRecentlyPaid(): Boolean {
        val lastPayment = payments.maxByOrNull { it.date }
        return lastPayment?.let {
            val daysSincePayment = ((Date().time - it.date.time) / (1000 * 60 * 60 * 24)).toInt()
            daysSincePayment <= 5
        } ?: false
    }
}

@Parcelize
enum class RepeatCycle(val displayName: String, val icon: String, val label: String) : Parcelable {
    DAILY("Harian", "🗓️", "Setiap Hari"),
    WEEKLY("Mingguan", "📅", "Setiap Minggu"),
    MONTHLY("Bulanan", "📆", "Setiap Bulan"),
    QUARTERLY("Per 3 Bulan", "🗂️", "Setiap 3 Bulan"),
    YEARLY("Tahunan", "📊", "Setiap Tahun")
}
