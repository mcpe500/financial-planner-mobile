package com.example.financialplannerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.Calendar
import java.util.UUID

@Parcelize
data class RecurringBill(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var estimatedAmount: Double,
    var dueDate: Date, // Represents the initial due date or the next due date if already started
    var repeatCycle: String, // Changed to String to remove RepeatCycle enum dependency
    var category: String? = null, // Optional: e.g., "Utilities", "Subscription", "Loan"
    var notes: String = "",
    var isActive: Boolean = true, // To easily deactivate a recurring bill without deleting
    var payments: List<BillPayment> = emptyList(), // History of payments for this bill
    var autoPay: Boolean = false, // If the bill is set to be paid automatically
    var notificationEnabled: Boolean = true, // If notifications are enabled for this bill
    var lastPaymentDate: Date? = null, // Date of the last payment made
    var creationDate: Date = Date() // Date when the recurring bill was created
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
            "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
            "QUARTERLY" -> calendar.add(Calendar.MONTH, 3)
            "YEARLY" -> calendar.add(Calendar.YEAR, 1)
            "DAILY" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
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
