package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*

class ReceiptRepositoryTest {

    @Test
    fun processReceiptData_shouldExtractCorrectInformation() {
        val receiptText = "WALMART\nItem 1: $10.99\nItem 2: $5.50\nTotal: $16.49"
        val receiptData = parseReceiptText(receiptText)
        
        assertEquals("WALMART", receiptData.merchant)
        assertEquals(16.49, receiptData.total, 0.01)
        assertEquals(2, receiptData.items.size)
    }

    @Test
    fun validateReceiptImage_shouldReturnTrueForValidBase64() {
        val validBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
        assertTrue(isValidBase64Image(validBase64))
    }

    @Test
    fun validateReceiptImage_shouldReturnFalseForInvalidBase64() {
        val invalidBase64 = "invalid-base64-string"
        assertFalse(isValidBase64Image(invalidBase64))
    }

    @Test
    fun extractReceiptItems_shouldParseItemsCorrectly() {
        val itemsText = "Item 1: $10.99\nItem 2: $5.50\nItem 3: $2.00"
        val items = extractItems(itemsText)
        
        assertEquals(3, items.size)
        assertEquals("Item 1", items[0].name)
        assertEquals(10.99, items[0].price, 0.01)
    }

    @Test
    fun calculateReceiptConfidence_shouldReturnHighConfidenceForClearData() {
        val receiptData = ReceiptData(
            merchant = "WALMART",
            total = 25.99,
            items = listOf(ReceiptItem("Item 1", 25.99))
        )
        val confidence = calculateConfidence(receiptData)
        assertTrue(confidence > 0.8)
    }

    @Test
    fun formatReceiptForStorage_shouldCreateCorrectFormat() {
        val receiptData = createMockReceiptData()
        val formatted = formatForStorage(receiptData)
        
        assertTrue(formatted.contains("merchant"))
        assertTrue(formatted.contains("total"))
        assertTrue(formatted.contains("items"))
    }

    private fun parseReceiptText(text: String): ReceiptData {
        val lines = text.split("\n")
        val merchant = lines.firstOrNull() ?: ""
        val totalLine = lines.find { it.contains("Total:") }
        val total = totalLine?.substringAfter("$")?.toDoubleOrNull() ?: 0.0
        val items = extractItems(text)
        
        return ReceiptData(merchant, total, items)
    }

    private fun isValidBase64Image(base64: String): Boolean {
        return try {
            base64.length > 10 && base64.matches(Regex("[A-Za-z0-9+/=]+"))
        } catch (e: Exception) {
            false
        }
    }

    private fun extractItems(text: String): List<ReceiptItem> {
        val itemPattern = Regex("(.+): \\$(\\d+\\.\\d{2})")
        return itemPattern.findAll(text).map { match ->
            ReceiptItem(match.groupValues[1], match.groupValues[2].toDouble())
        }.toList()
    }

    private fun calculateConfidence(data: ReceiptData): Double {
        var confidence = 0.0
        if (data.merchant.isNotEmpty()) confidence += 0.3
        if (data.total > 0) confidence += 0.4
        if (data.items.isNotEmpty()) confidence += 0.3
        return confidence
    }

    private fun formatForStorage(data: ReceiptData): String {
        return """
            {
                "merchant": "${data.merchant}",
                "total": ${data.total},
                "items": [${data.items.joinToString(",") { """{"name":"${it.name}","price":${it.price}}""" }}]
            }
        """.trimIndent()
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