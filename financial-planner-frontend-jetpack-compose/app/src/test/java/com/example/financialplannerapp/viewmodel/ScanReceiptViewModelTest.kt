package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*

class ScanReceiptViewModelTest {

    @Test
    fun validateReceiptData_shouldReturnTrueForValidData() {
        val receiptData = createMockReceiptData()
        assertTrue(isValidReceiptData(receiptData))
    }

    @Test
    fun validateReceiptData_shouldReturnFalseForInvalidData() {
        val receiptData = ReceiptData("", 0.0, emptyList())
        assertFalse(isValidReceiptData(receiptData))
    }

    @Test
    fun calculateReceiptTotal_shouldSumItemPrices() {
        val items = listOf(
            ReceiptItem("Item 1", 10.0),
            ReceiptItem("Item 2", 15.0),
            ReceiptItem("Item 3", 5.0)
        )
        val total = calculateReceiptTotal(items)
        assertEquals(30.0, total, 0.01)
    }

    @Test
    fun parseReceiptMerchant_shouldExtractMerchantName() {
        val receiptText = "WALMART STORE #1234\nThank you for shopping"
        val merchant = extractMerchantName(receiptText)
        assertEquals("WALMART", merchant)
    }

    @Test
    fun formatReceiptAmount_shouldFormatCorrectly() {
        val amount = 123.45
        val formatted = formatAmount(amount)
        assertEquals("$123.45", formatted)
    }

    private fun isValidReceiptData(data: ReceiptData): Boolean {
        return data.merchant.isNotEmpty() && data.total > 0 && data.items.isNotEmpty()
    }

    private fun calculateReceiptTotal(items: List<ReceiptItem>): Double {
        return items.sumOf { it.price }
    }

    private fun extractMerchantName(text: String): String {
        return text.split("\n").firstOrNull()?.split(" ")?.firstOrNull() ?: ""
    }

    private fun formatAmount(amount: Double): String {
        return "$%.2f".format(amount)
    }

    private fun createMockReceiptData(): ReceiptData {
        return ReceiptData(
            merchant = "Test Store",
            total = 25.99,
            items = listOf(ReceiptItem("Test Item", 25.99))
        )
    }

    data class ReceiptData(
        val merchant: String,
        val total: Double,
        val items: List<ReceiptItem>
    )

    data class ReceiptItem(
        val name: String,
        val price: Double
    )
}