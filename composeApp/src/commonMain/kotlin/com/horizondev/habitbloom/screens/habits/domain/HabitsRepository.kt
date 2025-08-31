package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.screens.calendar.HabitStreakInfo
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseStorageService
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.settings.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getNearestDateForNotification
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.core.component.KoinComponent

class HabitsRepository(
    private val remoteDataSource: HabitsRemoteDataSource,
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: HabitsLocalDataSource,
    private val storageService: SupabaseStorageService,
    private val notificationManager: NotificationScheduler,
    private val permissionsManager: PermissionsManager,
    private val flowerHealthRepository: FlowerHealthRepository
) : KoinComponent {
    private val TAG = "HabitsRepository"
    private val remoteHabits = MutableStateFlow<List<HabitInfo>>(emptyList())

    suspend fun initData(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Initialize Supabase storage bucket
                storageService.initializeBucket()

                // Wait for the user to be authenticated first
                val authResult = profileRemoteDataSource.isUserAuthenticated().getOrNull() ?: false

                if (!authResult) {
                    // If not authenticated, wait for authentication to complete
                    Napier.d("User not authenticated, authenticating...", tag = TAG)
                    val authSuccess =
                        profileRemoteDataSource.authenticateUser().getOrNull() ?: false

                    if (!authSuccess) {
                        Napier.e("Failed to authenticate user", tag = TAG)
                        return@withContext Result.failure(Exception("Authentication failed"))
                    }
                }

                // Now that we're authenticated, fetch habits
                Napier.d("User authenticated, fetching habits...", tag = TAG)

                getAllHabits().onSuccess { habits ->
                    remoteHabits.update { habits }
                }.map { true }
            } catch (e: Exception) {
                Napier.e("Error in initData", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    private suspend fun getAllHabits(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            Napier.d("Fetching network habits...", tag = TAG)

            // Get user ID, with retry mechanism if needed
            val userId = runCatching {
                val user = profileRemoteDataSource.getUser().getOrNull()

                if (user == null) {
                    // If no user found, try one more authentication attempt
                    profileRemoteDataSource.authenticateUser()
                    profileRemoteDataSource.getUser().getOrNull()?.id
                } else {
                    user.id
                }
            }.getOrNull()

            if (userId == null) {
                Napier.e("Failed to get user ID for habits", tag = TAG)
                return@withContext Result.failure(Exception("User ID is null"))
            }

            remoteDataSource.getHabits(userId)
        }
    }

    suspend fun getHabits(
        searchInput: String,
        categoryId: String? = null
    ): Result<List<HabitInfo>> {
        return getAllHabits().mapCatching { remoteHabits ->
            remoteHabits.filter { habit ->
                categoryId == null || habit.categoryId == categoryId
            }.filter {
                it.name.lowercase().contains(searchInput.lowercase())
            }
        }
    }

    suspend fun getHabitCategories(): Result<List<HabitCategoryData>> {
        return remoteDataSource.getHabitCategories()
    }

    suspend fun getHabitIcons(): Result<List<String>> {
        return remoteDataSource.getHabitIcons()
    }

    fun getUserHabitsByDayFlow(day: LocalDate): Flow<List<UserHabitRecordFullInfo>> {
        return combine(
            remoteHabits.filter { it.isEmpty().not() },
            localDataSource.getUserHabitsByDateFlow(day)
        ) { detailedHabits, habitRecords ->
            Napier.d("getUserHabitsByDayFlow $habitRecords", tag = TAG)

            mergeLocalHabitRecordsWithRemote(
                habitRecords = habitRecords,
                detailedHabits = detailedHabits,
                untilDate = day
            )
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    /**
     * Updates an existing habit with new duration and days.
     *
     * @param userHabitId The ID of the user habit to update
     * @param endDate The new end date for the habit
     * @param days The days of the week the habit should occur on
     * @return Result containing success (true) or failure with error
     */
    suspend fun updateExistingHabit(
        userHabitId: Long,
        endDate: LocalDate,
        days: List<DayOfWeek>
    ): Result<Boolean> {
        return runCatching {
            localDataSource.updateUserHabit(
                userHabitId = userHabitId,
                endDate = endDate,
                days = days
            )
        }.map { true }
    }

    suspend fun updateHabitCompletionByRecordId(
        habitRecordId: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        // Get the user habit ID for this record
        val record = localDataSource.getHabitRecordByRecordId(habitRecordId) ?: return

        // Update the record completion status
        localDataSource.updateHabitCompletionByRecordId(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = isCompleted
        )

        // Update flower health based on completion status
        if (isCompleted) {
            flowerHealthRepository.updateHealthForCompletedHabit(record.userHabitId)
        } else {
            flowerHealthRepository.updateHealthForMissedHabit(record.userHabitId)
        }
    }

    suspend fun updateHabitCompletionByHabitId(
        habitId: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        // Update the record completion status
        localDataSource.updateHabitCompletionByHabitId(
            habitId = habitId,
            date = date,
            isCompleted = isCompleted
        )

        // Update flower health based on completion status
        if (isCompleted) {
            flowerHealthRepository.updateHealthForCompletedHabit(habitId)
        } else {
            flowerHealthRepository.updateHealthForMissedHabit(habitId)
        }
    }

    suspend fun createPersonalHabit(
        userId: String,
        title: String,
        description: String,
        categoryId: String? = null,
        icon: String = DEFAULT_PHOTO_URL
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            // Save the habit with the icon URL (either direct URL or uploaded image URL)
            remoteDataSource.savePersonalHabit(
                userId = userId,
                title = title,
                description = description,
                categoryId = categoryId,
                icon = icon
            ).onSuccess {
                getAllHabits().onSuccess { habits ->
                    remoteHabits.update { habits }
                }.map { true }
            }
        }
    }

    fun getListOfAllUserHabitRecordsFlow(
        untilDate: LocalDate = getCurrentDate()
    ): Flow<List<UserHabitRecordFullInfo>> {
        return combine(
            flow { emit(getAllHabits()) },
            localDataSource.getAllUserHabitRecords(untilDate)
        ) { allHabitsResult, localHabitRecords ->
            val allHabits = allHabitsResult.getOrThrow()
            mergeLocalHabitRecordsWithRemote(
                detailedHabits = allHabits,
                habitRecords = localHabitRecords,
                untilDate = untilDate
            )
        }
    }

    fun getUserHabitWithAllRecordsFlow(
        userHabitId: Long
    ): Flow<UserHabitFullInfo?> {
        return combine(
            flow { emit(getAllHabits()) },
            localDataSource.getAllUserHabitRecordsForHabitId(userHabitId)
        ) { allHabitsResult, localHabitRecords ->
            val allHabits = allHabitsResult.getOrNull() ?: emptyList()
            val userHabitInfo = localDataSource.getUserHabitInfo(userHabitId) ?: return@combine null
            val originId = localDataSource.getHabitOriginId(userHabitId)


            val habitDetailedInfo = allHabits.find {
                it.id == originId
            } ?: return@combine null

            val currentStreak = localDataSource.getHabitDayStreak(
                userHabitId = userHabitId,
                byDate = getCurrentDate()
            )

            UserHabitFullInfo(
                userHabitId = userHabitId,
                description = habitDetailedInfo.description,
                iconUrl = habitDetailedInfo.iconUrl,
                name = habitDetailedInfo.name,
                timeOfDay = habitDetailedInfo.timeOfDay,
                daysStreak = currentStreak,
                records = localHabitRecords,
                startDate = userHabitInfo.startDate,
                days = userHabitInfo.daysOfWeek,
                reminderTime = userHabitInfo.reminderTime,
                reminderEnabled = userHabitInfo.reminderEnabled,
                endDate = userHabitInfo.endDate
            )
        }.flowOn(Dispatchers.IO)
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
                timeOfDay = habitDetailedInfo.timeOfDay,
                daysStreak = localDataSource.getHabitDayStreak(
                    userHabitId = userHabitId,
                    byDate = untilDate
                )
            )
        }
    }

    suspend fun deleteUserHabit(
        userHabitId: Long
    ) = runCatching {
        localDataSource.deleteUserHabit(userHabitId)
    }

    suspend fun getFutureDaysForHabit(
        userHabitId: Long,
        fromDate: LocalDate = getCurrentDate()
    ): List<LocalDate> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllUserHabitRecordsForHabitId(userHabitId).first()
                .map { it.date }
                .filter { it >= fromDate }
                .sortedBy { it }
        }
    }

    /**
     * Clears all past records for a specific habit up to the current date.
     * Current and future records are preserved.
     *
     * @param userHabitId The ID of the user habit
     * @return Result containing the number of records deleted on success, or the error on failure
     */
    suspend fun clearPastRecords(userHabitId: Long): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val currentDate = getCurrentDate()
                val count = localDataSource.clearPastRecords(userHabitId, currentDate)
                Napier.d("Cleared $count past records for habit $userHabitId", tag = TAG)

                // Update any stateful values for this habit if needed
                // This ensures that completed repeats are recalculated

                Result.success(count)
            } catch (e: Exception) {
                Napier.e("Failed to clear past records for habit $userHabitId", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Deletes a custom habit by its ID.
     *
     * @param habitId The ID of the custom habit to delete
     * @return Result containing success (true) or failure with error
     */
    suspend fun deleteCustomHabit(habitId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // First try to delete from remote storage
                remoteDataSource.deleteCustomHabit(habitId).fold(
                    onSuccess = {
                        // If successful, update the cached habits list
                        val updatedHabits = remoteHabits.value.filter { it.id != habitId }
                        remoteHabits.update { updatedHabits }
                        Result.success(true)
                    },
                    onFailure = { error ->
                        Napier.e("Failed to delete custom habit: ${error.message}", tag = TAG)
                        Result.failure(error)
                    }
                )
            } catch (e: Exception) {
                Napier.e("Error deleting custom habit", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Adds a new habit with the specified details.
     *
     * @param habitInfo The habit information
     * @param startDate The start date for the habit
     * @param endDate The end date for the habit
     * @param selectedDays The days of the week the habit should occur on
     * @param reminderEnabled Whether reminder notifications are enabled
     * @param reminderTime The time to send reminder notifications
     * @return Result containing the ID of the newly created habit or an error
     */
    suspend fun addUserHabit(
        habitInfo: HabitInfo,
        startDate: LocalDate,
        endDate: LocalDate,
        selectedDays: List<DayOfWeek> = emptyList(),
        reminderEnabled: Boolean = false,
        reminderTime: LocalTime? = null
    ): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Use provided days or default to all days
                val days = selectedDays.ifEmpty { DayOfWeek.entries }

                // Create UserHabit object with reminder settings
                val userHabit = UserHabit(
                    id = 0L,
                    habitId = habitInfo.id,
                    startDate = startDate,
                    endDate = endDate,
                    daysOfWeek = days,
                    timeOfDay = habitInfo.timeOfDay,
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderTime
                )

                // Insert the habit and return the ID
                val habitId = localDataSource.insertUserHabit(
                    userHabit = userHabit
                )

                Result.success(habitId)
            } catch (e: Exception) {
                Napier.e("Error adding user habit", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Schedules a reminder for a specific habit
     */
    suspend fun scheduleReminderForHabit(
        habitId: Long,
        reminderTime: LocalTime
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!permissionsManager.hasNotificationPermission()) {
                    val permissionGranted = permissionsManager.requestNotificationPermission()
                    if (!permissionGranted) {
                        return@withContext Result.failure(Exception("Notification permission denied"))
                    }
                }

                val habitInfo = getUserHabitWithAllRecordsFlow(habitId).first()
                    ?: return@withContext Result.failure(Exception("Invalid habitId"))

                val habitDates = getFutureDaysForHabit(habitId)
                val nearestDate = getNearestDateForNotification(
                    dates = habitDates,
                    notificationTime = reminderTime
                )

                if (nearestDate == null) {
                    return@withContext Result.failure(Exception("No future available dates for such habit notification"))
                }

                val success = notificationManager.scheduleHabitReminder(
                    habitId = habitId,
                    habitName = habitInfo.name,
                    description = habitInfo.description,
                    time = reminderTime,
                    date = nearestDate
                )

                success
            }.onFailure {
                Napier.e("Error scheduling reminder", it, tag = TAG)
            }
        }
    }

    /**
     * Updates the reminder settings for an existing habit
     *
     * @param habitId The ID of the habit
     * @param enabled Whether the reminder is enabled
     * @param reminderTime The time for the reminder (null to keep existing)
     * @return Result containing success (true) or failure with error
     */
    suspend fun updateHabitReminder(
        habitId: Long,
        enabled: Boolean,
        reminderTime: LocalTime?
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                localDataSource.updateHabitReminder(
                    habitId = habitId,
                    enabled = enabled,
                    reminderTime = reminderTime
                )

                when {
                    enabled && reminderTime != null -> {
                        scheduleReminderForHabit(habitId, reminderTime).getOrThrow()
                    }

                    else -> {
                        notificationManager.cancelHabitReminder(habitId)
                        true
                    }
                }

            }.onFailure {
                Napier.e("Error updating habit reminder", it, tag = TAG)
            }
        }
    }

    /**
     * Gets basic habit details for notification purposes
     *
     * @param habitId The ID of the user habit to get details for
     * @return Basic habit details or null if not found
     */
    suspend fun getUserHabitDetails(habitId: Long): HabitNotificationDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val userHabit = localDataSource.getUserHabitInfo(habitId) ?: return@withContext null
                val originId = localDataSource.getHabitOriginId(habitId)
                val habitInfo = remoteHabits.value.find { it.id == originId }

                HabitNotificationDetails(
                    id = habitId,
                    name = habitInfo?.name ?: "Habit Reminder",
                    description = habitInfo?.description ?: "Time for your habit!",
                    activeDays = userHabit.daysOfWeek,
                    reminderEnabled = userHabit.reminderEnabled,
                    reminderTime = userHabit.reminderTime
                )
            } catch (e: Exception) {
                Napier.e("Error getting habit details", e, tag = TAG)
                null
            }
        }
    }

    fun calculateHabitStreak(
        userHabitId: Long,
        habitRecords: List<UserHabitRecordFullInfo>
    ): HabitStreakInfo {
        val today = getCurrentDate()
        val records = habitRecords.filter { it.userHabitId == userHabitId }

        val habitName = records.firstOrNull()?.name ?: "Unknown Habit"

        // Sort records by date (newest first)
        val sortedRecords = records
            .sortedByDescending { it.date }
            .filter { it.date <= today }

        // Calculate current streak
        var currentStreak = 0
        var previousDate: LocalDate? = null

        for (record in sortedRecords) {
            if (!record.isCompleted) continue

            // If this is the first completed habit we're examining
            if (previousDate == null) {
                previousDate = record.date
                currentStreak = 1
                continue
            }

            // Check if this record is part of a consecutive streak
            val expectedDate = previousDate.minus(1, DateTimeUnit.DAY)
            if (record.date == expectedDate) {
                currentStreak++
                previousDate = record.date
            } else {
                // The streak is broken
                break
            }
        }

        // Calculate longest streak (historical)
        var longestStreak = 0
        var currentLongestStreak = 0
        var lastDate: LocalDate? = null

        for (record in records.sortedBy { it.date }) {
            if (!record.isCompleted) {
                // Reset current streak if we find an uncompleted habit
                currentLongestStreak = 0
                lastDate = null
                continue
            }

            if (lastDate == null) {
                // First completed habit in a potential streak
                currentLongestStreak = 1
                lastDate = record.date
            } else {
                val expectedDate = lastDate.plus(1, DateTimeUnit.DAY)
                if (record.date == expectedDate) {
                    // Streak continues
                    currentLongestStreak++
                } else {
                    // Streak breaks, start a new one
                    currentLongestStreak = 1
                }

                lastDate = record.date
            }

            // Update longest streak if current is higher
            if (currentLongestStreak > longestStreak) {
                longestStreak = currentLongestStreak
            }
        }

        return HabitStreakInfo(
            userHabitId = userHabitId,
            habitName = habitName,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }

    /**
     * Data class with minimal habit details needed for notifications
     */
    data class HabitNotificationDetails(
        val id: Long,
        val name: String,
        val description: String,
        val activeDays: List<DayOfWeek>,
        val reminderEnabled: Boolean,
        val reminderTime: LocalTime?
    )

    /**
     * Calculates the number of days between two dates, inclusive.
     */
    private fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Int {
        if (endDate < startDate) return 0

        var days = 0
        var currentDate = startDate

        while (currentDate <= endDate) {
            days++
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        return days
    }

    /**
     * Gets all user habit records directly (non-reactive).
     *
     * @return List of all user habit records
     */
    suspend fun getListOfAllUserHabitRecords(): List<UserHabitRecordFullInfo> =
        withContext(Dispatchers.IO) {
            // Get basic record data from local source
            val localRecords = localDataSource.getAllUserHabitRecordsWithInfo()

            // Get remote habit data (should be cached)
            val detailedHabits = remoteHabits.value

            // Merge local and remote data
            localRecords.mapNotNull { record ->
                // Get the remote habit ID for this record
                val originHabitId =
                    localDataSource.getHabitOriginId(record.userHabitId) ?: return@mapNotNull null

                // Find the detailed habit info
                val habitDetailedInfo =
                    detailedHabits.find { it.id == originHabitId } ?: return@mapNotNull null

                // Calculate current streak
                val streakDays = localDataSource.getHabitDayStreak(
                    userHabitId = record.userHabitId,
                    byDate = getCurrentDate()
                )

                // Create the full record with merged data
                UserHabitRecordFullInfo(
                    id = record.id,
                    userHabitId = record.userHabitId,
                    date = record.date,
                    isCompleted = record.isCompleted,
                    name = habitDetailedInfo.name,
                    description = habitDetailedInfo.description,
                    iconUrl = habitDetailedInfo.iconUrl,
                    timeOfDay = habitDetailedInfo.timeOfDay,
                    daysStreak = streakDays
                )
            }
    }

    /**
     * Gets all user habit records for a specific time of day (non-reactive).
     *
     * @param timeOfDay The time of day to filter by
     * @return List of habit records for the specified time of day
     */
    suspend fun getHabitRecordsByTimeOfDay(timeOfDay: TimeOfDay): List<UserHabitRecordFullInfo> =
        withContext(Dispatchers.IO) {
            // Get all habit records
            val records = localDataSource.getAllUserHabitRecords(getCurrentDate()).first()

            // Get all remote habit info (should be cached in remoteHabits)
            val detailedHabits = remoteHabits.value

            // Filter and merge with remote data
            val resultList = mutableListOf<UserHabitRecordFullInfo>()

            // Group records by habit ID
            val recordsByHabitId = records.groupBy { it.userHabitId }

            // Process each habit
            recordsByHabitId.forEach { (userHabitId, habitRecords) ->
                // Get remote habit ID
                val originHabitId = localDataSource.getHabitOriginId(userHabitId) ?: return@forEach

                // Find detailed habit info
                val habitDetailedInfo =
                    detailedHabits.find { it.id == originHabitId } ?: return@forEach

                // Filter by time of day
                if (habitDetailedInfo.timeOfDay != timeOfDay) return@forEach

                // Calculate streak
                val streakDays = localDataSource.getHabitDayStreak(userHabitId, getCurrentDate())

                // Create record objects
                habitRecords.forEach { record ->
                    resultList.add(
                        UserHabitRecordFullInfo(
                            id = record.id,
                            userHabitId = record.userHabitId,
                            date = record.date,
                            isCompleted = record.isCompleted,
                            name = habitDetailedInfo.name,
                            description = habitDetailedInfo.description,
                            iconUrl = habitDetailedInfo.iconUrl,
                            timeOfDay = habitDetailedInfo.timeOfDay,
                            daysStreak = streakDays
                        )
                    )
                }
            }

            resultList
        }

    /**
     * Checks if a habit is already added by the user
     *
     * @param habitId The ID of the habit to check
     * @return True if the habit is already added, false otherwise
     */
    suspend fun isHabitAlreadyAdded(habitId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val existingHabit = localDataSource.getUserHabitByRemoteId(habitId)
            existingHabit != null
        }
    }

    /**
     * Gets a list of all user habits without detailed information.
     * This is a lightweight method for getting just the basic habit information.
     *
     * @return List of UserHabit objects
     */
    suspend fun getUserHabitsWithoutDetails(): List<UserHabit> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllUserHabits()
        }
    }
}