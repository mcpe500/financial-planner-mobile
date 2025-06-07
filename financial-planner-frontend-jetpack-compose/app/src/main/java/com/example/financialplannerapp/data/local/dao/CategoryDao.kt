package com.example.financialplannerapp.data.local.dao

import androidx.room.*
import com.example.financialplannerapp.data.local.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?
    
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>
    
    @Update
    suspend fun updateCategory(category: CategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
    
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)
    
    @Query("UPDATE categories SET isActive = 0 WHERE id = :id")
    suspend fun deactivateCategory(id: Int)
    
    @Query("UPDATE categories SET isActive = 1 WHERE id = :id")
    suspend fun activateCategory(id: Int)
    
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}
