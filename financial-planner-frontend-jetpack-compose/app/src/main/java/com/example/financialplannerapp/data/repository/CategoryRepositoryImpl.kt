package com.example.financialplannerapp.data.repository

import com.example.financialplannerapp.data.local.dao.CategoryDao
import com.example.financialplannerapp.data.local.model.CategoryEntity
import com.example.financialplannerapp.data.model.CategoryData
import com.example.financialplannerapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl constructor(
    private val categoryDao: CategoryDao,
    private val apiService: ApiService
) : CategoryRepository {

    override fun getAllActiveCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllActiveCategories()
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoryById(id: Int): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun getCategoryByName(name: String): CategoryEntity? {
        return categoryDao.getCategoryByName(name)
    }

    override suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    override suspend fun insertCategories(categories: List<CategoryEntity>): List<Long> {
        return categoryDao.insertCategories(categories)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    override suspend fun deleteCategoryById(id: Int) {
        categoryDao.deleteCategoryById(id)
    }

    override suspend fun deactivateCategory(id: Int) {
        categoryDao.deactivateCategory(id)
    }

    override suspend fun activateCategory(id: Int) {
        categoryDao.activateCategory(id)
    }

    override suspend fun syncCategoriesFromRemote(): Result<List<CategoryData>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val categories = response.body()!!
                // Convert CategoryData to CategoryEntity and save to local database
                val entities = categories.map { categoryData ->
                    CategoryEntity(
                        id = categoryData.id,
                        name = categoryData.name,
                        description = categoryData.description,
                        colorCode = null,
                        iconName = null,
                        isActive = true,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                categoryDao.insertCategories(entities)
                Result.success(categories)
            } else {
                Result.failure(Exception("Failed to sync categories: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadCategoriesToRemote(categories: List<CategoryEntity>): Result<Unit> {
        return try {
            // Convert CategoryEntity to CategoryData for API
            val categoryDataList = categories.map { entity ->
                CategoryData(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description
                )
            }
            
            val response = apiService.uploadCategories(categoryDataList)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to upload categories: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
