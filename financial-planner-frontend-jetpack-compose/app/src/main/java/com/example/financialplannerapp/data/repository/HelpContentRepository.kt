package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.db.HelpContentDao
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent

class HelpContentRepository(private val helpContentDao: HelpContentDao) {

    // FAQ Functions
    suspend fun insertAllFAQs(faqs: List<FAQItem>) {
        helpContentDao.insertAllFAQs(faqs)
    }

    suspend fun getFaqsByCategory(category: String): List<FAQItem> {
        return helpContentDao.getFAQsByCategory(category)
    }

    suspend fun searchFAQs(query: String): List<FAQItem> {
        return helpContentDao.searchFAQs(query)
    }

    suspend fun getFaqLastUpdateTime(): Long? {
        return helpContentDao.getFAQLastUpdateTime()
    }

    suspend fun clearAllFAQs() {
        helpContentDao.clearAllFAQs()
    }

    // HelpContent Functions
    suspend fun insertAllHelpContent(helpContents: List<HelpContent>) {
        helpContentDao.insertAllHelpContent(helpContents)
    }

    suspend fun getHelpContentByCategory(category: String): List<HelpContent> {
        return helpContentDao.getHelpContentByCategory(category)
    }

    suspend fun searchHelpContent(query: String): List<HelpContent> {
        return helpContentDao.searchHelpContent(query)
    }

    suspend fun getHelpContentLastUpdateTime(category: String): Long? {
        return helpContentDao.getLastUpdateTime(category)
    }

    suspend fun clearAllHelpContent() {
        helpContentDao.clearAllHelpContent()
    }
}