package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.model.CategoryEntity
import com.example.financialplannerapp.data.model.CategoryData
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllActiveCategories(): Flow<List<CategoryEntity>>
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun getCategoryById(id: Int): CategoryEntity?
    suspend fun getCategoryByName(name: String): CategoryEntity?
    suspend fun insertCategory(category: CategoryEntity): Long
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>
    suspend fun updateCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)
    suspend fun deleteCategoryById(id: Int)
    suspend fun deactivateCategory(id: Int)
    suspend fun activateCategory(id: Int)
    suspend fun syncCategoriesFromRemote(): Result<List<CategoryData>>
    suspend fun uploadCategoriesToRemote(categories: List<CategoryEntity>): Result<Unit>
}
