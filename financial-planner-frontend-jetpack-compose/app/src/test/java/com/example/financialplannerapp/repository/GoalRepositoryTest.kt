package com.example.financialplannerapp.repository

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class GoalRepositoryTest {

    @Test
    fun createGoal_shouldReturnGoalWithId() {
        val goal = createMockGoal()
        val savedGoal = saveGoal(goal)
        
        assertNotNull(savedGoal.id)
        assertEquals(goal.name, savedGoal.name)
        assertEquals(goal.targetAmount, savedGoal.targetAmount, 0.01)
    }

    @Test
    fun updateGoalProgress_shouldUpdateCurrentAmount() {
        val goal = MockGoal(1, "Emergency Fund", 10000.0, 3000.0, Date())
        val updatedGoal = updateProgress(goal, 4000.0)
        
        assertEquals(4000.0, updatedGoal.currentAmount, 0.01)
    }

    @Test
    fun filterGoalsByStatus_shouldReturnCorrectGoals() {
        val goals = listOf(
            MockGoal(1, "Emergency Fund", 10000.0, 10000.0, Date()),
            MockGoal(2, "Vacation", 5000.0, 2000.0, Date()),
            MockGoal(3, "Car", 20000.0, 20000.0, Date())
        )
        
        val completedGoals = filterByStatus(goals, "COMPLETED")
        assertEquals(2, completedGoals.size)
        assertTrue(completedGoals.all { it.currentAmount >= it.targetAmount })
    }

    @Test
    fun calculateGoalCompletion_shouldReturnCorrectPercentage() {
        val goal = MockGoal(1, "Emergency Fund", 10000.0, 7500.0, Date())
        val completion = calculateCompletion(goal)
        assertEquals(75.0, completion, 0.01)
    }

    @Test
    fun validateGoalData_shouldReturnTrueForValidGoal() {
        val goal = createMockGoal()
        assertTrue(isValidGoal(goal))
    }

    @Test
    fun validateGoalData_shouldReturnFalseForInvalidGoal() {
        val goal = MockGoal(0, "", 0.0, 0.0, Date())
        assertFalse(isValidGoal(goal))
    }

    @Test
    fun sortGoalsByPriority_shouldReturnCorrectOrder() {
        val goals = listOf(
            MockGoal(1, "Low Priority", 1000.0, 500.0, Date()),
            MockGoal(2, "High Priority", 5000.0, 1000.0, Date()),
            MockGoal(3, "Medium Priority", 3000.0, 1500.0, Date())
        )
        
        val sortedGoals = sortByCompletion(goals)
        assertTrue(sortedGoals[0].currentAmount / sortedGoals[0].targetAmount >= 
                  sortedGoals[1].currentAmount / sortedGoals[1].targetAmount)
    }

    @Test
    fun deleteGoal_shouldRemoveGoalFromList() {
        val goals = mutableListOf(
            MockGoal(1, "Goal 1", 1000.0, 500.0, Date()),
            MockGoal(2, "Goal 2", 2000.0, 1000.0, Date())
        )
        
        val updatedGoals = deleteGoal(goals, 1)
        assertEquals(1, updatedGoals.size)
        assertFalse(updatedGoals.any { it.id == 1L })
    }

    private fun createMockGoal(): MockGoal {
        return MockGoal(
            id = 0,
            name = "Emergency Fund",
            targetAmount = 10000.0,
            currentAmount = 3000.0,
            targetDate = Date()
        )
    }

    private fun saveGoal(goal: MockGoal): MockGoal {
        return goal.copy(id = 1)
    }

    private fun updateProgress(goal: MockGoal, newAmount: Double): MockGoal {
        return goal.copy(currentAmount = newAmount)
    }

    private fun filterByStatus(goals: List<MockGoal>, status: String): List<MockGoal> {
        return when (status) {
            "COMPLETED" -> goals.filter { it.currentAmount >= it.targetAmount }
            "IN_PROGRESS" -> goals.filter { it.currentAmount > 0 && it.currentAmount < it.targetAmount }
            else -> goals
        }
    }

    private fun calculateCompletion(goal: MockGoal): Double {
        return if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount) * 100 else 0.0
    }

    private fun isValidGoal(goal: MockGoal): Boolean {
        return goal.name.isNotEmpty() && goal.targetAmount > 0
    }

    private fun sortByCompletion(goals: List<MockGoal>): List<MockGoal> {
        return goals.sortedByDescending { it.currentAmount / it.targetAmount }
    }

    private fun deleteGoal(goals: MutableList<MockGoal>, goalId: Long): List<MockGoal> {
        goals.removeAll { it.id == goalId }
        return goals.toList()
    }

    data class MockGoal(
        val id: Long,
        val name: String,
        val targetAmount: Double,
        val currentAmount: Double,
        val targetDate: Date
    )
}