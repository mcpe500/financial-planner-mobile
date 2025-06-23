package com.example.financialplannerapp.data.model

import android.os.Parcelable
import com.example.financialplannerapp.data.local.model.BillEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.Calendar
import java.util.UUID

@Parcelize
data class RecurringBill(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var estimatedAmount: Double,
    var dueDate: Date,
    var repeatCycle: String,
    var category: String? = null,
    var notes: String = "",
    var isActive: Boolean = true,
    var payments: List<BillPayment> = emptyList(),
    var autoPay: Boolean = false,
    var notificationEnabled: Boolean = true,
    var lastPaymentDate: Date? = null,
    var creationDate: Date = Date()
) : Parcelable {
    val nextDueDate: Date get() = calculateNextDueDate()
    val isOverdue: Boolean get() = Date().after(nextDueDate) && !isPaidInCurrentCycle()
    val daysToDue: Int get() = ((nextDueDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
    val status: BillStatus get() = when {
        isPaidInCurrentCycle() -> BillStatus.PAID
        isOverdue -> BillStatus.OVERDUE
        daysToDue <= 7 -> BillStatus.DUE_SOON
        !isActive -> BillStatus.PAID
        else -> BillStatus.UPCOMING
    }

    private fun calculateNextDueDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = dueDate
        while (calendar.time.before(Date())) {
             when (repeatCycle) {
                "DAILY" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
                "QUARTERLY" -> calendar.add(Calendar.MONTH, 3)
                "YEARLY" -> calendar.add(Calendar.YEAR, 1)
                else -> break // Break for non-repeating or unknown cycles
            }
        }
        return calendar.time
    }

    private fun isPaidInCurrentCycle(): Boolean {
        val lastPayment = payments.maxByOrNull { it.date } ?: return false
        val cycleStart = Calendar.getInstance()
        cycleStart.time = nextDueDate // Start from the next due date and go back

        when (repeatCycle) {
            "DAILY" -> cycleStart.add(Calendar.DAY_OF_YEAR, -1)
            "WEEKLY" -> cycleStart.add(Calendar.WEEK_OF_YEAR, -1)
            "MONTHLY" -> cycleStart.add(Calendar.MONTH, -1)
            "QUARTERLY" -> cycleStart.add(Calendar.MONTH, -3)
            "YEARLY" -> cycleStart.add(Calendar.YEAR, -1)
        }
        // If the last payment date is after the calculated start of the current cycle, it's paid.
        return lastPayment.date.after(cycleStart.time)
    }

    companion object {
        fun fromEntity(entity: BillEntity): RecurringBill {
            val paymentType = object : TypeToken<List<BillPayment>>() {}.type
            val paymentsList = try {
                Gson().fromJson<List<BillPayment>>(entity.paymentsJson, paymentType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            return RecurringBill(
                id = entity.uuid,
                name = entity.name,
                estimatedAmount = entity.estimatedAmount,
                dueDate = entity.dueDate,
                repeatCycle = entity.repeatCycle,
                category = entity.category,
                notes = entity.notes,
                isActive = entity.isActive,
                payments = paymentsList,
                autoPay = entity.autoPay,
                notificationEnabled = entity.notificationEnabled,
                lastPaymentDate = entity.lastPaymentDate,
                creationDate = entity.creationDate
            )
        }
    }
}