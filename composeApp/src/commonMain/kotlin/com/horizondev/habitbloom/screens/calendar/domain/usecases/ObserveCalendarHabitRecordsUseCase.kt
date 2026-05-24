package com.horizondev.habitbloom.screens.calendar.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

class ObserveCalendarHabitRecordsUseCase(
    private val repository: HabitsRepository
) {
    operator fun invoke(): Flow<List<UserHabitRecordFullInfo>> {
        return repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
    }
}
