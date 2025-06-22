package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class BillViewModelTest {

    @Test
    fun calculateNextDueDate_shouldReturnCorrectDate() {
        val lastPaid = Calendar.getInstance()
        lastPaid.set(2024, 0, 15) // January 15, 2024
        val frequency = 30 // Monthly
        
        val nextDue = calculateNextDueDate(lastPaid.time, frequency)
        val expectedDate = Calendar.getInstance()
        expectedDate.set(2024, 1, 14) // February 14, 2024
        
        assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH), 
                    Calendar.getInstance().apply { time = nextDue }.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun validateBillData_shouldReturnTrueForValidBill() {
        val bill = createValidBill()
        assertTrue(isValidBill(bill))
    }

    @Test
    fun validateBillData_shouldReturnFalseForInvalidBill() {
        val bill = Bill("", 0.0, Date(), false)
        assertFalse(isValidBill(bill))
    }

    @Test
    fun checkOverdueBills_shouldReturnTrueForOverdueBill() {
        val pastDate = Calendar.getInstance()
        pastDate.add(Calendar.DAY_OF_MONTH, -5)
        val bill = Bill("Electric Bill", 100.0, pastDate.time, false)
        
        assertTrue(isOverdue(bill))
    }

    @Test
    fun checkOverdueBills_shouldReturnFalseForFutureBill() {
        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.DAY_OF_MONTH, 5)
        val bill = Bill("Electric Bill", 100.0, futureDate.time, false)
        
        assertFalse(isOverdue(bill))
    }

    @Test
    fun calculateTotalMonthlyBills_shouldReturnCorrectAmount() {
        val bills = listOf(
            Bill("Electric", 100.0, Date(), false),
            Bill("Water", 50.0, Date(), false),
            Bill("Internet", 80.0, Date(), false)
        )
        val total = calculateMonthlyTotal(bills)
        assertEquals(230.0, total, 0.01)
    }

    @Test
    fun markBillAsPaid_shouldUpdateBillStatus() {
        val bill = Bill("Electric Bill", 100.0, Date(), false)
        val paidBill = markAsPaid(bill)
        assertTrue(paidBill.isPaid)
    }

    @Test
    fun getBillsByStatus_shouldFilterCorrectly() {
        val bills = listOf(
            Bill("Electric", 100.0, Date(), true),
            Bill("Water", 50.0, Date(), false),
            Bill("Internet", 80.0, Date(), false)
        )
        
        val unpaidBills = filterByStatus(bills, false)
        assertEquals(2, unpaidBills.size)
        assertTrue(unpaidBills.all { !it.isPaid })
    }

    private fun createValidBill(): Bill {
        return Bill(
            name = "Electric Bill",
            amount = 100.0,
            dueDate = Date(),
            isPaid = false
        )
    }

    private fun calculateNextDueDate(lastPaid: Date, frequencyDays: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = lastPaid
        calendar.add(Calendar.DAY_OF_MONTH, frequencyDays)
        return calendar.time
    }

    private fun isValidBill(bill: Bill): Boolean {
        return bill.name.isNotEmpty() && bill.amount > 0
    }

    private fun isOverdue(bill: Bill): Boolean {
        return !bill.isPaid && bill.dueDate.before(Date())
    }

    private fun calculateMonthlyTotal(bills: List<Bill>): Double {
        return bills.sumOf { it.amount }
    }

    private fun markAsPaid(bill: Bill): Bill {
        return bill.copy(isPaid = true)
    }

    private fun filterByStatus(bills: List<Bill>, isPaid: Boolean): List<Bill> {
        return bills.filter { it.isPaid == isPaid }
    }

    data class Bill(
        val name: String,
        val amount: Double,
        val dueDate: Date,
        val isPaid: Boolean
    )
}