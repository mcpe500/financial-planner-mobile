package com.example.financialplannerapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.example.financialplannerapp.models.roomdb.HelpContent

@Dao
interface HelpContentDao {

    // FAQItem Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFAQs(faqs: List<FAQItem>)

    @Query("SELECT * FROM faq_items WHERE category = :category ORDER BY `order` ASC")
    suspend fun getFAQsByCategory(category: String): List<FAQItem>

    @Query("SELECT * FROM faq_items WHERE question LIKE '%' || :query || '%' OR answer LIKE '%' || :query || '%' ORDER BY `order` ASC")
    suspend fun searchFAQs(query: String): List<FAQItem>

    @Query("SELECT MAX(lastUpdated) FROM faq_items")
    suspend fun getFAQLastUpdateTime(): Long?

    @Query("DELETE FROM faq_items")
    suspend fun clearAllFAQs()

    // HelpContent Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHelpContent(helpContents: List<HelpContent>)

    @Query("SELECT * FROM help_content WHERE category = :category ORDER BY `order` ASC")
    suspend fun getHelpContentByCategory(category: String): List<HelpContent>

    @Query("SELECT * FROM help_content WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY `order` ASC")
    suspend fun searchHelpContent(query: String): List<HelpContent>

    @Query("SELECT MAX(lastUpdated) FROM help_content WHERE category = :category")
    suspend fun getLastUpdateTime(category: String): Long?

    @Query("DELETE FROM help_content")
    suspend fun clearAllHelpContent()
}