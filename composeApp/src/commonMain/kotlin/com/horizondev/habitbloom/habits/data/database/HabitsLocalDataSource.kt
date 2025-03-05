package com.horizondev.habitbloom.habits.data.database

import app.cash.sqldelight.coroutines.asFlow
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.habits.domain.models.UserHabit
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.mapToString
import com.horizondev.habitbloom.utils.plusDays
import database.UserHabitRecordsEntityQueries
import database.UserHabitsEntityQueries
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.math.max

class HabitsLocalDataSource(
    private val database: HabitBloomDatabase,
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

    suspend fun getHabitOriginId(userHabitId: Long): String? {
        return withContext(Dispatchers.IO) {
            val allHabits = userHabitsQueries.selectAllUserHabitsEntity().executeAsList()
            Napier.d("getHabitOriginId all habits $allHabits", tag = "TAG")
            userHabitsQueries.selectUserHabitById(userHabitId).executeAsOneOrNull()?.habitId
        }
    }

    suspend fun getUserHabitInfo(userHabitId: Long): UserHabit? {
        return withContext(Dispatchers.IO) {
            userHabitsQueries.selectUserHabitById(userHabitId).executeAsOneOrNull()?.toDomainModel()
        }
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

    suspend fun updateUserHabit(
        userHabitId: Long,
        allRepeats: Int,
        repeatsToChangeRecords: Int,
        days: List<DayOfWeek>,
        updateAfterDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        database.transaction {
            val existingHabit = userHabitsQueries.selectUserHabitById(
                userHabitId
            ).executeAsOneOrNull()

            if (existingHabit == null) throw IllegalStateException()

            userHabitsQueries.updateUserHabitById(
                startDate = existingHabit.startDate,
                repeats = allRepeats.toLong(),
                daysOfWeek = days.mapToString(),
                id = existingHabit.id
            )

            val existingHabitRecords =
                userHabitRecordsQueries.selectUserHabitRecordsEntityByUserHabitId(
                    userHabitId = userHabitId
                ).executeAsList()

            val todayDate = getCurrentDate()
            val isHabitCompletedToday = existingHabitRecords.find {
                it.date == todayDate.toString()
            }?.isCompleted ?: 0

            //update outdated habit records
            userHabitRecordsQueries.deleteUserHabitRecordsEntityAfterSpecificDateByUserHabitId(
                userHabitId = userHabitId,
                date = todayDate.toString()
            )

            val habitDates = generateHabitDates(
                startDate = updateAfterDate,
                habitDays = days.sortedBy { it.ordinal },
                repeats = repeatsToChangeRecords
            )

            habitDates.forEach { date ->
                val isCompleted = when (date) {
                    todayDate -> isHabitCompletedToday
                    else -> 0
                }
                userHabitRecordsQueries.insertOrReplaceUserHabitRecord(
                    userHabitId = userHabitId,
                    date = date.toString(),
                    isCompleted = isCompleted
                )
            }
        }
    }

    suspend fun insertUserHabit(userHabit: UserHabit) = withContext(Dispatchers.IO) {
        val existingHabit = userHabitsQueries.selectUserHabitByRemoteId(
            userHabit.habitId
        ).executeAsOneOrNull()

        val localHabitId = if (existingHabit != null) {
            val existingStartDate = LocalDate.parse(existingHabit.startDate)
            val mergedStartDay = when {
                existingStartDate < userHabit.startDate -> existingStartDate
                else -> userHabit.startDate
            }
            val repeats = max(existingHabit.repeats, userHabit.repeats.toLong())
            val existingDays = existingHabit.daysOfWeek.split(",").map {
                DayOfWeek.valueOf(it)
            }
            val mergedDays = (existingDays + userHabit.daysOfWeek)
                .toSet()
                .sortedBy { it.ordinal }
                .joinToString(",") { it.name }

            userHabitsQueries.updateUserHabitById(
                startDate = mergedStartDay.toString(),
                repeats = repeats,
                daysOfWeek = mergedDays,
                id = existingHabit.id
            )
            existingHabit.id
        } else {
            userHabitsQueries.insertUserHabit(
                habitId = userHabit.habitId,
                startDate = userHabit.startDate.toString(),
                repeats = userHabit.repeats.toLong(),
                daysOfWeek = userHabit.daysOfWeek.joinToString(",") { it.name },
                timeOfDay = userHabit.timeOfDay.ordinal.toLong()
            )
            val lastInsertRowId = userHabitsQueries
                .selectUserHabitByRemoteId(userHabit.habitId)
                .executeAsOneOrNull()?.id ?: 0

            Napier.d("lastInsertRowId $lastInsertRowId for userHabit $userHabit", tag = "TAG")
            lastInsertRowId
        }

        // Generate habit records
        generateHabitRecords(localHabitId, userHabit)
    }

    private suspend fun generateHabitRecords(userHabitId: Long, userHabit: UserHabit) =
        withContext(Dispatchers.IO) {
            val existingHabitRecords =
                userHabitRecordsQueries.selectUserHabitRecordsEntityByUserHabitId(
                    userHabitId = userHabitId
                ).executeAsList()

            val habitDates = generateHabitDates(
                startDate = userHabit.startDate,
                habitDays = userHabit.daysOfWeek.sortedBy { it.ordinal },
                repeats = userHabit.repeats
            )

            habitDates.forEach { date ->
                val isRecordExistAlready = existingHabitRecords.any { it.date == date.toString() }

                if (isRecordExistAlready.not()) {
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

    suspend fun deleteUserHabit(userHabitId: Long) {
        withContext(Dispatchers.IO) {
            database.transaction {
                userHabitsQueries.deleteUserHabitById(userHabitId)
                userHabitRecordsQueries.deleteUserHabitRecordsEntityByUserHabitId(userHabitId)
            }
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

    fun getAllUserHabitRecords(untilDate: LocalDate): Flow<List<UserHabitRecord>> {
        return userHabitRecordsQueries
            .selectAllUserHabitRecords()
            .asFlow()
            .mapToList()
            .map { rows ->
                rows.map { row ->
                    row.toDomainModel()
                }.filter { it.date <= untilDate }
            }
    }

    fun getAllUserHabitRecordsForHabitId(userHabitId: Long): Flow<List<UserHabitRecord>> {
        return userHabitRecordsQueries
            .selectUserHabitRecordsEntityByUserHabitId(userHabitId)
            .asFlow()
            .mapToList()
            .map { rows ->
                rows.map { row ->
                    row.toDomainModel()
                }
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

    /**
     * Clears all past habit records for a user habit up to a specific date (exclusive).
     * Current date and future records are preserved.
     *
     * @param userHabitId The ID of the user habit
     * @param currentDate The date threshold (records before this date will be deleted)
     * @return The number of records that were deleted
     */
    suspend fun clearPastRecords(userHabitId: Long, currentDate: LocalDate): Int {
        return withContext(Dispatchers.IO) {
            // First count how many records will be affected
            val recordsToDelete = userHabitRecordsQueries
                .selectUserHabitRecordsEntityByUserHabitId(userHabitId)
                .executeAsList()
                .count { LocalDate.parse(it.date) < currentDate }

            // Then perform the deletion
            database.transaction {
                userHabitRecordsQueries.clearPastRecordsBeforeDate(
                    userHabitId = userHabitId,
                    date = currentDate.toString()
                )
            }

            recordsToDelete
        }
    }
}