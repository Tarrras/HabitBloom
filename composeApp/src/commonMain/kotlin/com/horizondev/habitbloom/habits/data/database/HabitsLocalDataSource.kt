package com.horizondev.habitbloom.habits.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.horizondev.habitbloom.habits.domain.models.UserHabit
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import database.UserHabitRecordsEntityQueries
import database.UserHabitsEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class HabitsLocalDataSource(
    private val userHabitsQueries: UserHabitsEntityQueries,
    private val userHabitRecordsQueries: UserHabitRecordsEntityQueries
) {

    suspend fun updateHabitCompletion(userHabitId: Long, date: LocalDate, isCompleted: Boolean) {
        userHabitRecordsQueries.updateUserHabitRecordCompletion(
            isCompleted = if (isCompleted) 1L else 2L,
            date = date.toString(),
            userHabitId = userHabitId
        )
    }

    suspend fun getHabitOriginId(userHabitId: Long): String {
        return userHabitsQueries.selectUserHabitById(userHabitId).executeAsOne().habitId
    }

    suspend fun insertUserHabit(userHabit: UserHabit) {
        userHabitsQueries.insertUserHabit(
            habitId = userHabit.habitId,
            startDate = userHabit.startDate.toString(),
            duration = userHabit.duration.toLong(),
            daysOfWeek = userHabit.daysOfWeek.joinToString(",") { it.name },
            timeOfDay = userHabit.timeOfDay.ordinal.toLong()
        )

        // Get the auto-generated ID of the inserted UserHabit
        val userHabitId = userHabitsQueries.lastInsertRowId().executeAsOne()

        // Generate habit records
        generateHabitRecords(userHabitId, userHabit)
    }

    private suspend fun generateHabitRecords(userHabitId: Long, userHabit: UserHabit) {
        val endDate = userHabit.startDate.plus(userHabit.duration - 1, DateTimeUnit.DAY)
        val scheduledDays = userHabit.daysOfWeek.toSet()

        var date = userHabit.startDate
        while (date <= endDate) {
            if (date.dayOfWeek in scheduledDays) {
                userHabitRecordsQueries.insertOrReplaceUserHabitRecord(
                    userHabitId = userHabitId,
                    date = date.toString(),
                    isCompleted = 0  // Not completed by default
                )
            }
            date = date.plus(1, DateTimeUnit.DAY)
        }
    }

    suspend fun getUserHabitsByDate(date: LocalDate): List<UserHabitRecord> {
        return userHabitRecordsQueries
            .selectUserHabitRecordsEntityByDate(date.toString())
            .executeAsList()
            .map { row ->
                UserHabitRecord(
                    id = row.id,
                    userHabitId = row.userHabitId,
                    date = LocalDate.parse(row.date),
                    isCompleted = row.isCompleted == 1L
                )
            }
    }

    fun getUserHabitsByDateFlow(date: LocalDate): Flow<List<UserHabitRecord>> {
        return userHabitRecordsQueries
            .selectUserHabitRecordsEntityByDate(date.toString())
            .asFlow()
            .mapToList()
            .map { rows ->
                rows.map { row ->
                    UserHabitRecord(
                        id = row.id,
                        userHabitId = row.userHabitId,
                        date = LocalDate.parse(row.date),
                        isCompleted = row.isCompleted == 1L
                    )
                }
            }
    }
}