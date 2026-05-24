package com.horizondev.habitbloom.screens.statistic.domain.usecases

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

class ObserveStatisticHabitRecordsUseCase(
    private val repository: HabitsRepository
) {
    operator fun invoke(): Flow<List<UserHabitRecordFullInfo>> {
        return repository.getListOfAllUserHabitRecordsFlow(
            untilDate = getCurrentDate().plus(DatePeriod(years = 1))
        )
    }
}
