package com.horizondev.habitbloom.habits.data.database

import app.cash.sqldelight.coroutines.asFlow
import com.horizondev.habitbloom.habits.domain.models.UserHabit
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.plusDays
import database.UserHabitRecordsEntityQueries
import database.UserHabitsEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class HabitsLocalDataSource(
    private val userHabitsQueries: UserHabitsEntityQueries,
    private val userHabitRecordsQueries: UserHabitRecordsEntityQueries
) {

    suspend fun updateHabitCompletion(habitRecordId: Long, date: LocalDate, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            userHabitRecordsQueries.updateUserHabitRecordCompletion(
                isCompleted = if (isCompleted) 1L else 2L,
                date = date.toString(),
                id = habitRecordId
            )
        }
    }

    suspend fun getHabitOriginId(userHabitId: Long): String {
        return userHabitsQueries.selectUserHabitById(userHabitId).executeAsOne().habitId
    }

    suspend fun getHabitDayStreak(
        userHabitId: Long,
        byDate: LocalDate,
        includingToday: Boolean = true
    ): Int {
        val habitRecords = withContext(Dispatchers.IO) {
            userHabitRecordsQueries
                .selectUserHabitRecordsEntityByUserHabitId(userHabitId)
                .executeAsList()
        }
        val mappedHabits = habitRecords
            .asSequence()
            .map { it.toDomainModel() }

        val isCompletedToday = mappedHabits.find {
            it.date == getCurrentDate()
        }?.isCompleted ?: false

        val completedBefore = mappedHabits
            .filter { it.date < byDate }
            .sortedByDescending { it.date }
            .takeWhile { it.isCompleted }
            .count()

        return completedBefore + when {
            includingToday -> if (isCompletedToday) 1 else 0
            else -> 0
        }
    }

    suspend fun insertUserHabit(userHabit: UserHabit) {
        userHabitsQueries.insertUserHabit(
            habitId = userHabit.habitId,
            startDate = userHabit.startDate.toString(),
            repeats = userHabit.repeats.toLong(),
            daysOfWeek = userHabit.daysOfWeek.joinToString(",") { it.name },
            timeOfDay = userHabit.timeOfDay.ordinal.toLong()
        )

        // Get the auto-generated ID of the inserted UserHabit
        val userHabitId = userHabitsQueries.lastInsertRowId().executeAsOne()

        // Generate habit records
        generateHabitRecords(userHabitId, userHabit)
    }

    private suspend fun generateHabitRecords(userHabitId: Long, userHabit: UserHabit) {
        val habitDates = generateHabitDates(
            startDate = userHabit.startDate,
            habitDays = userHabit.daysOfWeek.sortedBy { it.ordinal },
            repeats = userHabit.repeats
        )

        withContext(Dispatchers.IO) {
            habitDates.forEach { date ->
                userHabitRecordsQueries.insertOrReplaceUserHabitRecord(
                    userHabitId = userHabitId,
                    date = date.toString(),
                    isCompleted = 0  // Not completed by default
                )
            }
        }
    }

    private fun generateHabitDates(
        startDate: LocalDate,
        habitDays: List<DayOfWeek>,
        repeats: Int
    ): List<LocalDate> {
        val habitDates = mutableListOf<LocalDate>()

        val startOfFirstWeek = startDate.calculateStartOfWeek()

        for (weekOffset in 0 until repeats) {
            val startOfWeek = startOfFirstWeek.plus(
                value = weekOffset.toLong(), unit = DateTimeUnit.WEEK
            )

            for (dayOfWeek in habitDays) {
                val date = startOfWeek.plusDays(dayOfWeek.ordinal.toLong())
                if (date >= startDate) {
                    habitDates.add(date)
                }
            }
        }

        return habitDates
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

    suspend fun getAllUserHabitRecords(untilDate: LocalDate): List<UserHabitRecord> {
        return withContext(Dispatchers.IO) {
            userHabitRecordsQueries
                .selectAllUserHabitRecords()
                .executeAsList()
                .map { row ->
                    row.toDomainModel()
                }
                .filter { it.date <= untilDate }
        }
    }

    fun getUserHabitsByDateFlow(date: LocalDate): Flow<List<UserHabitRecord>> {
        return userHabitRecordsQueries
            .selectUserHabitRecordsEntityByDate(date.toString())
            .asFlow()
            .mapToList()
            .map { rows ->
                rows.map { row ->
                    row.toDomainModel()
                }
            }
    }
}