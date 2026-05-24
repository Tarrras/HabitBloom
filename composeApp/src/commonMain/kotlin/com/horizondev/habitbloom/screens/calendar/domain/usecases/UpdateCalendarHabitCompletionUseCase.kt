package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.datetime.LocalDate

class UpdateCalendarHabitCompletionUseCase(
    private val repository: HabitsRepository
) {
    suspend operator fun invoke(
        habitRecordId: Long,
        date: LocalDate,
        completed: Boolean
    ) {
        if (date != getCurrentDate()) return

        repository.updateHabitCompletionByRecordId(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = completed
        )
    }
}
