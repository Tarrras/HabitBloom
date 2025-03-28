package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.screens.calendar.HabitStreakInfo
import com.horizondev.habitbloom.screens.garden.data.FlowerHealthDataSource
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseStorageService
import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabit
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.screens.settings.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.calculateCompletedRepeats
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getNearestDateForNotification
import com.russhwolf.settings.ObservableSettings
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
    private val settings: ObservableSettings,
    private val flowerHealthDataSource: FlowerHealthDataSource
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

    suspend fun getHabits(searchInput: String, timeOfDay: TimeOfDay): Result<List<HabitInfo>> {
        return getAllHabits().mapCatching { remoteHabits ->
            remoteHabits.filter {
                it.timeOfDay == timeOfDay
            }.filter {
                it.name.lowercase().contains(searchInput.lowercase())
            }
        }
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

    suspend fun updateExistingHabit(
        userHabitId: Long,
        allRepeats: Int,
        repeatsToChangeRecords: Int,
        days: List<DayOfWeek>
    ): Result<Boolean> {
        return runCatching {
            localDataSource.updateUserHabit(
                userHabitId = userHabitId,
                allRepeats = allRepeats,
                repeatsToChangeRecords = repeatsToChangeRecords,
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
        val record = localDataSource.getAllUserHabitRecordsForHabitId(habitRecordId).first().first()
        // Update the record completion status
        localDataSource.updateHabitCompletionByRecordId(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = isCompleted
        )

        // Update flower health based on completion status
        if (isCompleted) {
            flowerHealthDataSource.updateHealthForCompletedHabit(record.userHabitId)
        } else {
            flowerHealthDataSource.updateHealthForMissedHabit(record.userHabitId)
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
            flowerHealthDataSource.updateHealthForCompletedHabit(habitId)
        } else {
            flowerHealthDataSource.updateHealthForMissedHabit(habitId)
        }
    }

    suspend fun createPersonalHabit(
        userId: String,
        timeOfDay: TimeOfDay,
        title: String,
        description: String,
        icon: String = DEFAULT_PHOTO_URL
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            // If icon is a local file path, upload it to Supabase Storage
            val iconUrl =
                if (icon.isNotEmpty() && (icon.startsWith("/") || icon.startsWith("file://"))) {
                    Napier.d("Uploading image from local path: $icon", tag = TAG)

                    // Upload the image file to Supabase Storage
                    storageService.uploadHabitImage(icon).fold(
                        onSuccess = { url ->
                            Napier.d(
                                "Image uploaded successfully to Supabase before adding habit",
                                tag = TAG
                            )
                            url
                        },
                        onFailure = { error ->
                            Napier.e(
                                "Failed to upload image to Supabase: ${error.message}",
                                tag = TAG
                            )
                            return@withContext Result.failure(error)
                        }
                    )
                } else {
                    icon
                }

            // Save the habit with the icon URL (either direct URL or uploaded image URL)
            remoteDataSource.savePersonalHabit(
                userId = userId,
                timeOfDay = timeOfDay,
                title = title,
                description = description,
                icon = iconUrl
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

            UserHabitFullInfo(
                userHabitId = userHabitId,
                description = habitDetailedInfo.description,
                iconUrl = habitDetailedInfo.iconUrl,
                name = habitDetailedInfo.name,
                timeOfDay = habitDetailedInfo.timeOfDay,
                daysStreak = localDataSource.getHabitDayStreak(
                    userHabitId = userHabitId,
                    byDate = getCurrentDate()
                ),
                records = localHabitRecords,
                startDate = userHabitInfo.startDate,
                days = userHabitInfo.daysOfWeek,
                repeats = userHabitInfo.repeats,
                completedRepeats = calculateCompletedRepeats(
                    dayOfCreation = userHabitInfo.startDate,
                    records = localHabitRecords
                ),
                reminderTime = userHabitInfo.reminderTime,
                reminderEnabled = userHabitInfo.reminderEnabled
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
     * Adds a habit for the user with the specified duration and start date.
     *
     * @param habitInfo The information about the habit to add
     * @param durationInDays The number of days the habit should run for
     * @param startDate The date when the habit should start
     * @param selectedDays The specific days of the week for the habit (optional)
     * @param reminderEnabled Whether a reminder should be set for this habit
     * @param reminderTime The time at which to send the reminder (if enabled)
     * @return Result containing the habit ID on success or failure with error
     */
    suspend fun addUserHabit(
        habitInfo: HabitInfo,
        durationInDays: Int,
        startDate: LocalDate,
        selectedDays: List<DayOfWeek> = DayOfWeek.entries,
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
                    repeats = durationInDays,
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
     * Gets the current flower health for a habit.
     *
     * @param habitId The ID of the habit
     * @return The flower health status
     */
    suspend fun getFlowerHealth(habitId: Long): FlowerHealth {
        return flowerHealthDataSource.getFlowerHealth(habitId)
    }

    /**
     * Observes flower health for a habit as a Flow.
     *
     * @param habitId The ID of the habit
     * @return Flow of FlowerHealth updates
     */
    fun observeFlowerHealth(habitId: Long): Flow<FlowerHealth> {
        return flowerHealthDataSource.observeFlowerHealth(habitId)
    }
}