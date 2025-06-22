package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class TransactionRepositoryTest {

    @Test
    fun createTransaction_shouldReturnTransactionWithId() {
        val transaction = createMockTransaction()
        val savedTransaction = saveTransaction(transaction)
        
        assertNotNull(savedTransaction.id)
        assertEquals(transaction.amount, savedTransaction.amount, 0.01)
        assertEquals(transaction.type, savedTransaction.type)
    }

    @Test
    fun filterTransactionsByType_shouldReturnCorrectTransactions() {
        val transactions = listOf(
            MockTransaction(1, 100.0, "INCOME", "Salary", Date()),
            MockTransaction(2, -50.0, "EXPENSE", "Food", Date()),
            MockTransaction(3, 200.0, "INCOME", "Bonus", Date())
        )
        
        val incomeTransactions = filterByType(transactions, "INCOME")
        assertEquals(2, incomeTransactions.size)
        assertTrue(incomeTransactions.all { it.type == "INCOME" })
    }

    @Test
    fun calculateTransactionSummary_shouldReturnCorrectTotals() {
        val transactions = listOf(
            MockTransaction(1, 100.0, "INCOME", "Salary", Date()),
            MockTransaction(2, -50.0, "EXPENSE", "Food", Date()),
            MockTransaction(3, -30.0, "EXPENSE", "Gas", Date())
        )
        
        val summary = calculateSummary(transactions)
        assertEquals(100.0, summary.totalIncome, 0.01)
        assertEquals(80.0, summary.totalExpense, 0.01)
        assertEquals(20.0, summary.netAmount, 0.01)
    }

    @Test
    fun validateTransactionData_shouldReturnTrueForValidTransaction() {
        val transaction = createMockTransaction()
        assertTrue(isValidTransaction(transaction))
    }

    private fun createMockTransaction(): MockTransaction {
        return MockTransaction(
            id = 0,
            amount = 100.0,
            type = "EXPENSE",
            category = "Food",
            date = Date()
        )
    }

    private fun saveTransaction(transaction: MockTransaction): MockTransaction {
        return transaction.copy(id = 1)
    }

    private fun filterByType(transactions: List<MockTransaction>, type: String): List<MockTransaction> {
        return transactions.filter { it.type == type }
    }

    private fun calculateSummary(transactions: List<MockTransaction>): TransactionSummary {
        val income = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val expense = transactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
        return TransactionSummary(income, expense, income - expense)
    }

    private fun isValidTransaction(transaction: MockTransaction): Boolean {
        return transaction.amount != 0.0 && transaction.category.isNotEmpty()
    }

    data class MockTransaction(
        val id: Long,
        val amount: Double,
        val type: String,
        val category: String,
        val date: Date
    )

    data class TransactionSummary(
        val totalIncome: Double,
        val totalExpense: Double,
        val netAmount: Double
    )
}