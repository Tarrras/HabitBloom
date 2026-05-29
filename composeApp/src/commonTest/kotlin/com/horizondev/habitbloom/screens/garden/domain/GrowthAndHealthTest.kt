package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun calculateLevelProgress_firstMissAfterPerfectHistoryDecaysFromFullVitality() {
        val records = listOf(
            UserHabitRecord(1, 10, LocalDate(2025, 1, 1), true),
            UserHabitRecord(2, 10, LocalDate(2025, 1, 2), true),
            UserHabitRecord(3, 10, LocalDate(2025, 1, 3), false)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertEquals(0.85f, progress.vitality)
        assertEquals(1, progress.currentMissedDays)
    }

    @Test
    fun calculateLevelProgress_missesDoNotRemoveXp() {
        val d1 = LocalDate(2025, 1, 1)
        val d2 = LocalDate(2025, 1, 2)
        val d3 = LocalDate(2025, 1, 3)
        val completedOnly = listOf(
            UserHabitRecord(1, 10, d1, true),
            UserHabitRecord(2, 10, d2, true)
        )
        val completedThenMissed = completedOnly + UserHabitRecord(3, 10, d3, false)

        val beforeMiss = calculateLevelProgress(records = completedOnly, daysPerWeek = 7)
        val afterMiss = calculateLevelProgress(records = completedThenMissed, daysPerWeek = 7)

        assertEquals(beforeMiss.totalXp, afterMiss.totalXp)
        assertEquals(beforeMiss.level, afterMiss.level)
        assertTrue(afterMiss.vitality < beforeMiss.vitality)
    }

    @Test
    fun calculateLevelProgress_currentMissedDaysTracksOnlyTrailingMisses() {
        val records = listOf(
            UserHabitRecord(1, 10, LocalDate(2025, 1, 1), true),
            UserHabitRecord(2, 10, LocalDate(2025, 1, 2), false),
            UserHabitRecord(3, 10, LocalDate(2025, 1, 3), false),
            UserHabitRecord(4, 10, LocalDate(2025, 1, 4), true)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertEquals(0, progress.currentMissedDays)
        assertTrue(progress.vitality in 0f..1f)
    }

    @Test
    fun calculateLevelProgress_currentMissedDaysCountsRecentMisses() {
        val records = listOf(
            UserHabitRecord(1, 10, LocalDate(2025, 1, 1), true),
            UserHabitRecord(2, 10, LocalDate(2025, 1, 2), false),
            UserHabitRecord(3, 10, LocalDate(2025, 1, 3), false)
        )

        val progress = calculateLevelProgress(records = records, daysPerWeek = 7)

        assertEquals(2, progress.currentMissedDays)
    }

    @Test
    fun calculateLevelProgress_shortCompletedHabitCanReachBloom() {
        val records = (1..5).map { day ->
            UserHabitRecord(
                id = day.toLong(),
                userHabitId = 10,
                date = LocalDate(2025, 1, day),
                isCompleted = true
            )
        }

        val progress = calculateLevelProgress(
            records = records,
            daysPerWeek = 7,
            expectedScheduledDays = records.size
        )

        assertEquals(5, progress.level)
        assertEquals(FlowerGrowthStage.BLOOM, levelToGrowthStage(progress.level))
    }

    @Test
    fun calculateLevelProgress_longHabitAccruesXpMoreSlowlyPerCompletion() {
        val completedDays = (1..5).map { day ->
            UserHabitRecord(
                id = day.toLong(),
                userHabitId = 10,
                date = LocalDate(2025, 1, day),
                isCompleted = true
            )
        }

        val shortHabit = calculateLevelProgress(
            records = completedDays,
            daysPerWeek = 7,
            expectedScheduledDays = 5
        )
        val longHabit = calculateLevelProgress(
            records = completedDays,
            daysPerWeek = 7,
            expectedScheduledDays = 30
        )

        assertTrue(shortHabit.totalXp > longHabit.totalXp)
        assertTrue(shortHabit.level > longHabit.level)
    }
}
