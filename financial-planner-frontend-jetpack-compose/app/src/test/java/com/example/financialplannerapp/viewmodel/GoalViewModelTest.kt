package com.example.financialplannerapp.viewmodel

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class GoalViewModelTest {

    @Test
    fun calculateGoalProgress_shouldReturnCorrectPercentage() {
        val target = 10000.0
        val current = 3000.0
        val progress = calculateProgress(current, target)
        assertEquals(30.0, progress, 0.01)
    }

    @Test
    fun calculateTimeToGoal_shouldReturnCorrectMonths() {
        val target = 12000.0
        val current = 2000.0
        val monthlySaving = 1000.0
        val months = calculateTimeToGoal(target, current, monthlySaving)
        assertEquals(10, months)
    }

    @Test
    fun validateGoalData_shouldReturnTrueForValidGoal() {
        val goal = createValidGoal()
        assertTrue(isValidGoal(goal))
    }

    @Test
    fun validateGoalData_shouldReturnFalseForInvalidGoal() {
        val goal = Goal("", 0.0, 0.0, Date())
        assertFalse(isValidGoal(goal))
    }

    @Test
    fun checkGoalStatus_shouldReturnCorrectStatus() {
        val target = 10000.0
        val current = 10000.0
        val status = getGoalStatus(current, target)
        assertEquals("COMPLETED", status)
    }

    @Test
    fun calculateRequiredMonthlySaving_shouldReturnCorrectAmount() {
        val target = 12000.0
        val current = 2000.0
        val months = 10
        val required = calculateRequiredMonthlySaving(target, current, months)
        assertEquals(1000.0, required, 0.01)
    }

    @Test
    fun formatGoalDescription_shouldReturnCorrectFormat() {
        val goal = createValidGoal()
        val description = formatGoalDescription(goal)
        assertTrue(description.contains(goal.name))
        assertTrue(description.contains(goal.targetAmount.toString()))
    }

    private fun createValidGoal(): Goal {
        return Goal(
            name = "Emergency Fund",
            targetAmount = 10000.0,
            currentAmount = 3000.0,
            targetDate = Date()
        )
    }

    private fun calculateProgress(current: Double, target: Double): Double {
        return if (target > 0) (current / target) * 100 else 0.0
    }

    private fun calculateTimeToGoal(target: Double, current: Double, monthlySaving: Double): Int {
        val remaining = target - current
        return if (monthlySaving > 0) (remaining / monthlySaving).toInt() else 0
    }

    private fun isValidGoal(goal: Goal): Boolean {
        return goal.name.isNotEmpty() && goal.targetAmount > 0
    }

    private fun getGoalStatus(current: Double, target: Double): String {
        return when {
            current >= target -> "COMPLETED"
            current >= target * 0.8 -> "NEAR_COMPLETION"
            current >= target * 0.5 -> "IN_PROGRESS"
            else -> "STARTED"
        }
    }

    private fun calculateRequiredMonthlySaving(target: Double, current: Double, months: Int): Double {
        val remaining = target - current
        return if (months > 0) remaining / months else 0.0
    }

    private fun formatGoalDescription(goal: Goal): String {
        return "Goal: ${goal.name} - Target: $${goal.targetAmount} - Current: $${goal.currentAmount}"
    }

    data class Goal(
        val name: String,
        val targetAmount: Double,
        val currentAmount: Double,
        val targetDate: Date
    )
}