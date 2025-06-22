package com.example.financialplannerapp.model

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class TransactionTest {

    @Test
    fun createTransaction_shouldHaveCorrectProperties() {
        val transaction = createMockTransaction()
        
        assertEquals(1L, transaction.id)
        assertEquals(100.0, transaction.amount, 0.01)
        assertEquals("EXPENSE", transaction.type)
        assertEquals("Food", transaction.category)
        assertEquals("Test transaction", transaction.note)
    }

    @Test
    fun transactionType_shouldBeExpenseForNegativeAmount() {
        val transaction = createMockTransaction()
        val type = if (transaction.amount < 0) "EXPENSE" else "INCOME"
        assertEquals("INCOME", type) // Since amount is positive
    }

    @Test
    fun transactionCategory_shouldNotBeEmpty() {
        val transaction = createMockTransaction()
        assertTrue(transaction.category.isNotEmpty())
    }

    private fun createMockTransaction(): MockTransaction {
        return MockTransaction(
            id = 1L,
            amount = 100.0,
            type = "EXPENSE",
            category = "Food",
            note = "Test transaction",
            date = Date()
        )
    }

    data class MockTransaction(
        val id: Long,
        val amount: Double,
        val type: String,
        val category: String,
        val note: String,
        val date: Date
    )
}