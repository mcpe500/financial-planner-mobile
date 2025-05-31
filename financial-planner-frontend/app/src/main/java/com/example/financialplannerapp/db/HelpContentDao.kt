package com.example.financialplannerapp.db

import androidx.room.*
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.example.financialplannerapp.models.roomdb.FAQItem

@Dao
interface HelpContentDao {
    
    // Help Content operations
    @Query("SELECT * FROM help_content WHERE category = :category ORDER BY `order` ASC")
    suspend fun getHelpContentByCategory(category: String): List<HelpContent>
    
    @Query("SELECT * FROM help_content WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    suspend fun searchHelpContent(query: String): List<HelpContent>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHelpContent(helpContent: HelpContent)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHelpContent(helpContentList: List<HelpContent>)
    
    @Query("DELETE FROM help_content WHERE category = :category")
    suspend fun deleteHelpContentByCategory(category: String)
    
    @Query("SELECT MAX(lastUpdated) FROM help_content WHERE category = :category")
    suspend fun getLastUpdateTime(category: String): Long?
    
    // FAQ operations
    @Query("SELECT * FROM faq_items WHERE category = :category ORDER BY `order` ASC")
    suspend fun getFAQsByCategory(category: String): List<FAQItem>
    
    @Query("SELECT * FROM faq_items WHERE isPopular = 1 ORDER BY `order` ASC LIMIT 10")
    suspend fun getPopularFAQs(): List<FAQItem>
    
    @Query("SELECT * FROM faq_items WHERE question LIKE '%' || :query || '%' OR answer LIKE '%' || :query || '%'")
    suspend fun searchFAQs(query: String): List<FAQItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFAQ(faqItem: FAQItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFAQs(faqList: List<FAQItem>)
    
    @Query("DELETE FROM faq_items WHERE category = :category")
    suspend fun deleteFAQsByCategory(category: String)
    
    @Query("SELECT MAX(lastUpdated) FROM faq_items")
    suspend fun getFAQLastUpdateTime(): Long?
}