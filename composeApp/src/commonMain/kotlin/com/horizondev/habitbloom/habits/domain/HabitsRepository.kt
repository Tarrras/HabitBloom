package com.horizondev.habitbloom.habits.domain

import com.horizondev.habitbloom.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.habits.data.remote.toDomainModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.domain.models.UserHabit
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class HabitsRepository(
    private val remoteDataSource: HabitsRemoteDataSource,
    private val localDataSource: HabitsLocalDataSource
) {
    private val TAG = "HabitsRepository"
    private val remoteHabits = MutableStateFlow<List<HabitInfo>>(emptyList())

    suspend fun initData(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            //remoteDataSource.pushHabitsToFirestore()
            getAllHabits().onSuccess { habits ->
                remoteHabits.update { habits }
            }.map { true }
        }
    }

    private suspend fun getAllHabits(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            Napier.d("Fetching network habits...", tag = TAG)
            remoteDataSource.getHabits()
                .mapCatching { data -> data.map { habit -> habit.toDomainModel() } }
        }
    }

    fun getHabits(searchInput: String, timeOfDay: TimeOfDay): List<HabitInfo> {
        return remoteHabits.value.filter {
            it.timeOfDay == timeOfDay
        }.filter {
            it.name.lowercase().contains(searchInput.lowercase())
        }
    }

    fun getUserHabitsByDayFlow(day: LocalDate): Flow<List<UserHabitRecordFullInfo>> {
        return combine(
            remoteHabits,
            localDataSource.getUserHabitsByDateFlow(day)
        ) { detailedHabits, habitRecords ->
            mergeLocalHabitRecordsWithRemote(
                habitRecords = habitRecords,
                detailedHabits = detailedHabits,
                untilDate = day
            )
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    suspend fun addHabit(
        habitInfo: HabitInfo,
        startDate: LocalDate,
        repeats: Int,
        days: List<DayOfWeek>
    ): Result<Boolean> {
        val userHabit = UserHabit(
            id = 0L,
            habitId = habitInfo.id,
            startDate = startDate,
            repeats = repeats,
            daysOfWeek = days,
            timeOfDay = habitInfo.timeOfDay
        )
        return runCatching {
            localDataSource.insertUserHabit(userHabit)
        }.map { true }
    }

    suspend fun updateHabitCompletion(habitRecordId: Long, date: LocalDate, isCompleted: Boolean) {
        localDataSource.updateHabitCompletion(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = isCompleted
        )
    }

    fun getListOfAllUserHabitRecordsFlow(): Flow<List<UserHabitRecordFullInfo>> {
        return combine(
            flow { emit(getAllHabits()) },
            localDataSource.getAllUserHabitRecords(getCurrentDate())
        ) { allHabitsResult, localHabitRecords ->
            val allHabits = allHabitsResult.getOrThrow()
            mergeLocalHabitRecordsWithRemote(
                detailedHabits = allHabits,
                habitRecords = localHabitRecords,
                untilDate = getCurrentDate()
            )
        }
    }

    private suspend fun mergeLocalHabitRecordsWithRemote(
        detailedHabits: List<HabitInfo>,
        habitRecords: List<UserHabitRecord>,
        untilDate: LocalDate
    ): List<UserHabitRecordFullInfo> {
        return habitRecords.mapNotNull { habitRecord ->
            val userHabitId = habitRecord.userHabitId
            val originHabitId = localDataSource.getHabitOriginId(userHabitId)

            val habitDetailedInfo = detailedHabits.find {
                it.id == originHabitId
            } ?: return@mapNotNull null

            UserHabitRecordFullInfo(
                id = habitRecord.id,
                userHabitId = habitRecord.userHabitId,
                date = habitRecord.date,
                isCompleted = habitRecord.isCompleted,
                description = habitDetailedInfo.description,
                iconUrl = habitDetailedInfo.iconUrl,
                name = habitDetailedInfo.name,
                shortInfo = habitDetailedInfo.shortInfo,
                timeOfDay = habitDetailedInfo.timeOfDay,
                daysStreak = localDataSource.getHabitDayStreak(
                    userHabitId = userHabitId,
                    byDate = untilDate
                )
            )
        }
    }
}