package com.horizondev.habitbloom.habits.domain

import com.horizondev.habitbloom.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.habits.data.remote.toDomainModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

class HabitsRepository(
    private val remoteDataSource: HabitsRemoteDataSource,
    private val localDataSource: HabitsLocalDataSource
) {
    private val remoteHabits = MutableStateFlow<List<HabitInfo>>(emptyList())

    suspend fun initData(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            getAllHabits().map {
                remoteHabits.update { it }
                true
            }
        }
    }

    private suspend fun getAllHabits(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getHabits()
                .mapCatching { data -> data.map { habit -> habit.toDomainModel() } }
        }
    }

    fun getUserHabitsByDayFlow(day: LocalDate): Flow<List<UserHabitRecordFullInfo>> {
        val detailedHabits = remoteHabits.value
        return localDataSource.getUserHabitsByDateFlow(day).map { habitRecords ->
            habitRecords.map { habitRecord ->
                val originHabitId = localDataSource.getHabitOriginId(habitRecord.id)

                val habitDetailedInfo = detailedHabits.find {
                    it.id == originHabitId
                } ?: return@map null

                UserHabitRecordFullInfo(
                    id = habitRecord.id,
                    userHabitId = habitRecord.userHabitId,
                    date = habitRecord.date,
                    isCompleted = habitRecord.isCompleted,
                    description = habitDetailedInfo.description,
                    iconUrl = habitDetailedInfo.iconUrl,
                    name = habitDetailedInfo.name,
                    shortInfo = habitDetailedInfo.shortInfo,
                    timeOfDay = habitDetailedInfo.timeOfDay
                )
            }.filterNotNull()
        }
    }


    suspend fun updateHabitCompletion(userHabitId: Long, date: LocalDate, isCompleted: Boolean) {
        localDataSource.updateHabitCompletion(
            userHabitId = userHabitId,
            date = date,
            isCompleted = isCompleted
        )
    }
}