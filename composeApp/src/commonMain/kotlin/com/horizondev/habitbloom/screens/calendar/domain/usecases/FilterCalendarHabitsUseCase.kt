package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.datetime.LocalDate

class FilterCalendarHabitsUseCase {
    operator fun invoke(
        habitsByDate: Map<LocalDate, List<UserHabitRecordFullInfo>>,
        selectedDate: LocalDate,
        timeOfDayFilter: TimeOfDay?
    ): List<UserHabitRecordFullInfo> {
        val habitsForDate = habitsByDate[selectedDate] ?: emptyList()

        return habitsForDate
            .asSequence()
            .filter { habit -> timeOfDayFilter == null || habit.timeOfDay == timeOfDayFilter }
            .sortedBy { habit -> habit.timeOfDay.ordinal }
            .toList()
    }
}
