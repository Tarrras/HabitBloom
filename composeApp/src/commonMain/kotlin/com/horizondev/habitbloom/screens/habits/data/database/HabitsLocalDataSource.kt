package com.horizondev.habitbloom.screens.habits.data.database

import app.cash.sqldelight.coroutines.asFlow
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.toTimeString
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.mapToString
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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class HabitsLocalDataSource(
    private val database: HabitBloomDatabase,
    private val userHabitsQueries: UserHabitsEntityQueries,
    private val userHabitRecordsQueries: UserHabitRecordsEntityQueries
) {

    suspend fun updateHabitCompletionByRecordId(
        habitRecordId: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        withContext(Dispatchers.IO) {
            userHabitRecordsQueries.updateUserHabitRecordCompletion(
                isCompleted = if (isCompleted) 1L else 2L,
                date = date.toString(),
                id = habitRecordId
            )
        }
    }

    suspend fun updateHabitCompletionByHabitId(
        habitId: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val recordByDate =
                userHabitRecordsQueries.selectUserHabitRecordsEntityByDate(date.toString())
                    .executeAsList()
                    .find { it.userHabitId == habitId }
                    ?.id ?: return@withContext

            userHabitRecordsQueries.updateUserHabitRecordCompletion(
                isCompleted = if (isCompleted) 1L else 2L,
                date = date.toString(),
                id = recordByDate
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
        endDate: LocalDate,
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
                endDate = endDate.toString(),
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
                endDate = endDate,
                habitDays = days.sortedBy { it.ordinal }
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

    suspend fun updateHabitReminder(
        habitId: Long,
        enabled: Boolean,
        reminderTime: LocalTime?
    ) = withContext(Dispatchers.IO) {
        userHabitsQueries.updateHabitReminder(
            id = habitId,
            reminderEnabled = if (enabled) 1L else 0L,
            reminderTime = reminderTime?.toTimeString()
        )
    }

    suspend fun insertUserHabit(userHabit: UserHabit) = withContext(Dispatchers.IO) {
        val existingHabit = userHabitsQueries.selectUserHabitByRemoteId(
            userHabit.habitId
        ).executeAsOneOrNull()

        val effectiveEndDate = userHabit.endDate

        val localHabitId = if (existingHabit != null) {
            val existingStartDate = LocalDate.parse(existingHabit.startDate)
            val existingEndDate = existingHabit.endDate.let { LocalDate.parse(it) }
            
            val mergedStartDay = when {
                existingStartDate < userHabit.startDate -> existingStartDate
                else -> userHabit.startDate
            }

            val mergedEndDate = when {
                existingEndDate != null && existingEndDate > effectiveEndDate -> existingEndDate
                else -> effectiveEndDate
            }
            
            val existingDays = existingHabit.daysOfWeek.split(",").map {
                DayOfWeek.valueOf(it)
            }
            val mergedDays = (existingDays + userHabit.daysOfWeek)
                .toSet()
                .sortedBy { it.ordinal }
                .joinToString(",") { it.name }

            userHabitsQueries.updateUserHabitById(
                startDate = mergedStartDay.toString(),
                endDate = mergedEndDate.toString(),
                daysOfWeek = mergedDays,
                id = existingHabit.id
            )
            existingHabit.id
        } else {
            userHabitsQueries.insertUserHabit(
                habitId = userHabit.habitId,
                startDate = userHabit.startDate.toString(),
                endDate = effectiveEndDate.toString(),
                daysOfWeek = userHabit.daysOfWeek.joinToString(",") { it.name },
                timeOfDay = userHabit.timeOfDay.ordinal.toLong(),
                reminderEnabled = if (userHabit.reminderEnabled) 1L else 0L,
                reminderTime = userHabit.reminderTime?.toTimeString()
            )
            val lastInsertRowId = userHabitsQueries
                .selectUserHabitByRemoteId(userHabit.habitId)
                .executeAsOneOrNull()?.id ?: 0

            Napier.d("lastInsertRowId $lastInsertRowId for userHabit $userHabit", tag = "TAG")
            lastInsertRowId
        }

        // Generate habit records
        return@withContext generateHabitRecords(localHabitId, userHabit, effectiveEndDate)
    }

    private suspend fun generateHabitRecords(
        userHabitId: Long,
        userHabit: UserHabit,
        effectiveEndDate: LocalDate
    ) = withContext(Dispatchers.IO) {
        val existingHabitRecords =
            userHabitRecordsQueries.selectUserHabitRecordsEntityByUserHabitId(
                userHabitId = userHabitId
            ).executeAsList()

        val habitDates = generateHabitDates(
            startDate = userHabit.startDate,
            endDate = effectiveEndDate,
            habitDays = userHabit.daysOfWeek.sortedBy { it.ordinal }
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

        return@withContext userHabitId
    }

    /**
     * Generate habit dates based on a date range and selected days of the week
     *
     * @param startDate The start date of the habit range
     * @param endDate The end date of the habit range
     * @param habitDays The days of the week the habit should occur on
     * @return List of dates that match the selected days within the date range
     */
    private fun generateHabitDates(
        startDate: LocalDate,
        endDate: LocalDate,
        habitDays: List<DayOfWeek>
    ): List<LocalDate> {
        val habitDates = mutableListOf<LocalDate>()

        // Generate a sequence of dates from start to end
        val dateRange = generateSequence(startDate) { date ->
            val next = date.plus(1, DateTimeUnit.DAY)
            if (next <= endDate) next else null
        }

        // Filter to include only dates that match the selected days of week
        dateRange.forEach { date ->
            if (habitDays.contains(date.dayOfWeek)) {
                habitDates.add(date)
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

    suspend fun getHabitRecordByRecordId(habitRecordId: Long): UserHabitRecord? {
        return withContext(Dispatchers.IO) {
            userHabitRecordsQueries
                .selectUserHabitRecordsEntityByUserHabitRecordId(habitRecordId)
                .executeAsOneOrNull()
                ?.let { item ->
                    UserHabitRecord(
                        id = item.id,
                        userHabitId = item.userHabitId,
                        date = LocalDate.parse(item.date),
                        isCompleted = item.isCompleted == 1L
                    )
                }
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

    /**
     * Retrieves all user habits.
     *
     * @return List of all user habits
     */
    suspend fun getAllUserHabits(): List<UserHabit> = withContext(Dispatchers.IO) {
        userHabitsQueries.selectAllUserHabitsEntity()
            .executeAsList()
            .map { it.toDomainModel() }
    }

    /**
     * Checks if a habit was completed on a specific date.
     *
     * @param userHabitId The ID of the habit to check
     * @param date The date to check for completion
     * @return true if the habit was completed on the specified date, false otherwise
     */
    suspend fun wasHabitCompletedOnDate(userHabitId: Long, date: LocalDate): Boolean =
        withContext(Dispatchers.IO) {
            // Query the record for this habit and date
            val record = userHabitRecordsQueries
                .selectUserHabitRecordsEntityByUserHabitId(userHabitId)
                .executeAsList()
                .find { it.date == date.toString() }

            // Check if the record exists and is marked as completed
            record?.isCompleted == 1L
        }

    /**
     * Gets all user habit records with their local data in a non-reactive way.
     *
     * Note: This doesn't include remote habit details (name, description, etc.)
     * which need to be merged at the repository level with data from remoteHabits.
     *
     * @return List of all user habit records with basic information
     */
    suspend fun getAllUserHabitRecordsWithInfo(): List<UserHabitRecord> =
        withContext(Dispatchers.IO) {
            // Get all habit records
            userHabitRecordsQueries
                .selectAllUserHabitRecords()
                .executeAsList()
                .map { record ->
                    UserHabitRecord(
                        id = record.id,
                        userHabitId = record.userHabitId,
                        date = LocalDate.parse(record.date),
                        isCompleted = record.isCompleted == 1L
                    )
                }
        }

    /**
     * Gets all user habit records for a specific habit within a date range.
     *
     * @param userHabitId The ID of the habit
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of habit records within the date range
     */
    suspend fun getUserHabitRecordsInDateRange(
        userHabitId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<UserHabitRecord> = withContext(Dispatchers.IO) {
        userHabitRecordsQueries
            .selectUserHabitRecordsEntityByUserHabitIdAndDateRange(
                userHabitId = userHabitId,
                date = startDate.toString(),
                date_ = endDate.toString()
            )
            .executeAsList()
            .map { record ->
                UserHabitRecord(
                    id = record.id,
                    userHabitId = record.userHabitId,
                    date = LocalDate.parse(record.date),
                    isCompleted = record.isCompleted == 1L
                )
            }
    }

    /**
     * Gets a user habit by its remote ID
     *
     * @param habitId The remote ID of the habit
     * @return The user habit or null if not found
     */
    suspend fun getUserHabitByRemoteId(habitId: String): UserHabit? = withContext(Dispatchers.IO) {
        userHabitsQueries
            .selectUserHabitByRemoteId(habitId)
            .executeAsOneOrNull()
            ?.let { entity ->
                entity.toDomainModel()
            }
    }
}