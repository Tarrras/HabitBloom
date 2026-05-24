package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.HabitStreak
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarUseCasesTest {

    @Test
    fun filterHabitsForDate_filtersByDateAndTimeOfDayThenSortsByTimeOfDay() {
        val morning = record(
            id = 1,
            userHabitId = 10,
            date = LocalDate(2026, 5, 22),
            timeOfDay = TimeOfDay.Morning
        )
        val evening = record(
            id = 2,
            userHabitId = 20,
            date = LocalDate(2026, 5, 22),
            timeOfDay = TimeOfDay.Evening
        )
        val afternoon = record(
            id = 3,
            userHabitId = 30,
            date = LocalDate(2026, 5, 22),
            timeOfDay = TimeOfDay.Afternoon
        )
        val otherDay = record(
            id = 4,
            userHabitId = 40,
            date = LocalDate(2026, 5, 23),
            timeOfDay = TimeOfDay.Morning
        )

        val result = FilterCalendarHabitsUseCase().invoke(
            habitsByDate = listOf(evening, otherDay, afternoon, morning).groupBy { it.date },
            selectedDate = LocalDate(2026, 5, 22),
            timeOfDayFilter = null
        )

        assertEquals(listOf(morning, afternoon, evening), result)
    }

    @Test
    fun calculateMonthlyStatistics_filtersByMonthAndTimeOfDayAndUsesStreakProvider() {
        val mayMorningCompleted = record(
            id = 1,
            userHabitId = 10,
            date = LocalDate(2026, 5, 1),
            completed = true,
            timeOfDay = TimeOfDay.Morning
        )
        val mayMorningMissed = record(
            id = 2,
            userHabitId = 20,
            date = LocalDate(2026, 5, 2),
            completed = false,
            timeOfDay = TimeOfDay.Morning
        )
        val mayEveningCompleted = record(
            id = 3,
            userHabitId = 30,
            date = LocalDate(2026, 5, 3),
            completed = true,
            timeOfDay = TimeOfDay.Evening
        )
        val juneMorningCompleted = record(
            id = 4,
            userHabitId = 40,
            date = LocalDate(2026, 6, 1),
            completed = true,
            timeOfDay = TimeOfDay.Morning
        )
        val habitsByDate = listOf(
            mayMorningCompleted,
            mayMorningMissed,
            mayEveningCompleted,
            juneMorningCompleted
        ).groupBy { it.date }
        val streakProvider = FakeCalendarHabitStreaksProvider(
            streaks = mapOf(
                10L to HabitStreak(10L, "Morning completed", currentStreak = 3),
                20L to HabitStreak(20L, "Morning missed", currentStreak = 1),
                30L to HabitStreak(30L, "Evening completed", currentStreak = 5)
            )
        )

        val result = CalculateCalendarMonthlyStatisticsUseCase(streakProvider).invoke(
            habitsByDate = habitsByDate,
            currentMonth = YearMonth(2026, 5),
            timeOfDayFilter = TimeOfDay.Morning
        )

        assertEquals(2, result.totalHabits)
        assertEquals(1, result.completedHabits)
        assertEquals(0.5f, result.completionRate)
        assertEquals(5, result.longestStreak)
        assertEquals(
            listOf(mayMorningCompleted, mayMorningMissed, mayEveningCompleted),
            streakProvider.receivedRecords
        )
    }

    private class FakeCalendarHabitStreaksProvider(
        private val streaks: Map<Long, HabitStreak>
    ) : CalendarHabitStreaksProvider {
        var receivedRecords: List<UserHabitRecordFullInfo> = emptyList()
            private set

        override fun calculate(
            habitRecords: List<UserHabitRecordFullInfo>
        ): Map<Long, HabitStreak> {
            receivedRecords = habitRecords
            return streaks
        }
    }

    private fun record(
        id: Long,
        userHabitId: Long,
        date: LocalDate,
        completed: Boolean = false,
        timeOfDay: TimeOfDay
    ): UserHabitRecordFullInfo = UserHabitRecordFullInfo(
        id = id,
        userHabitId = userHabitId,
        date = date,
        isCompleted = completed,
        description = "",
        iconUrl = "",
        name = "Habit $userHabitId",
        timeOfDay = timeOfDay,
        daysStreak = 0
    )
}
