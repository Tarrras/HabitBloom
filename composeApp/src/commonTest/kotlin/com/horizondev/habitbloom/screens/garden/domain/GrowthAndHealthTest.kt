package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GrowthAndHealthTest {

    @Test
    fun calculateLevelProgress_noRecords_returnsFullVitalityLevel1() {
        val progress = calculateLevelProgress(
            records = emptyList(),
            daysPerWeek = 7
        )

        assertEquals(1, progress.level)
        assertEquals(0, progress.totalXp)
        assertEquals(0, progress.xpInLevel)
        assertTrue(progress.xpForCurrentLevel > 0)
        assertEquals(1.0f, progress.vitality)
    }

    @Test
    fun calculateLevelProgress_singleCompletedToday_fullVitalityAndSomeXp() {
        val today = LocalDate(2025, 1, 1)
        val records = listOf(
            UserHabitRecord(id = 1, userHabitId = 10, date = today, isCompleted = true)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertEquals(1.0f, progress.vitality)
        assertTrue(progress.totalXp > 0)
        assertTrue(progress.xpInLevel > 0)
    }

    @Test
    fun calculateLevelProgress_allCompletedSoFar_fullVitality() {
        val d1 = LocalDate(2025, 1, 1)
        val d2 = LocalDate(2025, 1, 2)
        val d3 = LocalDate(2025, 1, 3)
        val records = listOf(
            UserHabitRecord(1, 10, d1, true),
            UserHabitRecord(2, 10, d2, true),
            UserHabitRecord(3, 10, d3, true)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertEquals(1.0f, progress.vitality)
        assertTrue(progress.totalXp > 0)
    }

    @Test
    fun calculateLevelProgress_withMisses_reducesVitality() {
        val d1 = LocalDate(2025, 1, 1)
        val d2 = LocalDate(2025, 1, 2)
        val d3 = LocalDate(2025, 1, 3)
        val records = listOf(
            UserHabitRecord(1, 10, d1, true),
            UserHabitRecord(2, 10, d2, false),
            UserHabitRecord(3, 10, d3, false)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertTrue(progress.vitality in 0f..1f)
        assertTrue(progress.vitality < 1.0f)
    }

    @Test
    fun flowerHealth_missPenalties_andRecovery_andRegressionRule() {
        var health = FlowerHealth(value = 1.0f, consecutiveMissedDays = 0)

        // 1st miss: lighter penalty
        health = health.habitMissed()
        assertEquals(0.85f, health.value)
        assertEquals(1, health.consecutiveMissedDays)
        assertFalse(health.isWilting) // healthy threshold 0.65
        assertFalse(health.isCritical)

        // 2nd miss: lighter penalty
        health = health.habitMissed()
        assertEquals(0.75f, health.value)
        assertEquals(2, health.consecutiveMissedDays)
        assertTrue(health.isWilting == false) // still above 0.65
        assertFalse(health.isCritical)
        assertFalse(health.shouldRegress())

        // 3rd miss: -0.08 => 0.67 -> rounds 0.7
        health = health.habitMissed()
        assertEquals(0.67f.roundToDecimal(1), health.value)
        assertEquals(3, health.consecutiveMissedDays)
        assertFalse(health.isWilting) // just above threshold
        assertFalse(health.isCritical)
        assertFalse(health.shouldRegress())

        // 4th miss: -0.08 => goes to ~0.59 (wilting)
        health = health.habitMissed()
        assertEquals(0.59f.roundToDecimal(1), health.value)
        assertEquals(4, health.consecutiveMissedDays)
        assertTrue(health.isWilting)
        assertFalse(health.isCritical)
        assertFalse(health.shouldRegress())

        // Recovery: +0.2 => bounce back and reset misses
        health = health.habitCompleted()
        assertEquals((0.59f + 0.2f).coerceAtMost(1.0f).roundToDecimal(1), health.value)
        assertEquals(0, health.consecutiveMissedDays)
        assertFalse(health.isCritical)
    }
}


