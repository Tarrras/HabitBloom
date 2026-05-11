package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.screens.calendar.HabitStreakInfo
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealthRepository
import com.horizondev.habitbloom.screens.habits.data.database.HabitCatalogLocalDataSource
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class HabitsRepository(
    private val remoteDataSource: HabitsRemoteDataSource,
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: HabitsLocalDataSource,
    private val habitCatalogLocalDataSource: HabitCatalogLocalDataSource,
    private val storageService: SupabaseStorageService,
    private val notificationManager: NotificationScheduler,
    private val permissionsManager: PermissionsManager,
    private val flowerHealthRepository: FlowerHealthRepository
) {
    private val TAG = "HabitsRepository"
    private val habitCatalogSyncMutex = Mutex()

    suspend fun initData(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Initialize Supabase storage bucket
                storageService.initializeBucket()

                refreshHabitCatalog().map { true }
            } catch (e: Exception) {
                Napier.e("Error in initData", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    private suspend fun getAuthenticatedUserId(): Result<String> {
        return withContext(Dispatchers.IO) {
            profileRemoteDataSource.getUser()
                .mapCatching { it.id }
                .recoverCatching {
                    Napier.d("User not authenticated, trying anonymous sign-in...", tag = TAG)
                    val authSuccess = profileRemoteDataSource.authenticateUser().getOrThrow()
                    if (!authSuccess) {
                        throw IllegalStateException("Authentication failed")
                    }
                    profileRemoteDataSource.getUser().getOrThrow().id
                }
        }
    }

    private suspend fun fetchRemoteHabitsFromNetwork(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            Napier.d("Fetching network habits...", tag = TAG)
            val userId = getAuthenticatedUserId().getOrElse { error ->
                Napier.e("Failed to get user ID for habits", error, tag = TAG)
                return@withContext Result.failure(error)
            }
            remoteDataSource.getHabits(userId)
        }
    }

    private suspend fun getHabitCatalogForRead(): Result<List<HabitInfo>> {
        val cachedHabits = habitCatalogLocalDataSource.getHabits()
        return if (cachedHabits.isNotEmpty()) {
            Result.success(cachedHabits)
        } else {
            refreshHabitCatalog()
        }
    }

    private suspend fun loadCatalogIfEmpty(flowName: String) {
        getHabitCatalogForRead().onFailure { error ->
            Napier.e("Failed to load habits catalog for $flowName", error, tag = TAG)
        }
    }

    private suspend fun getHabitCatalogOrEmpty(context: String): List<HabitInfo> {
        return getHabitCatalogForRead().getOrElse { error ->
            Napier.e("Failed to load habits catalog for $context", error, tag = TAG)
            emptyList()
        }
    }

    private suspend fun refreshHabitCatalog(): Result<List<HabitInfo>> {
        return habitCatalogSyncMutex.withLock {
            val cachedHabits = habitCatalogLocalDataSource.getHabits()
            val remoteResult = fetchRemoteHabitsFromNetwork()
            resolveCatalogRefreshResult(
                cachedHabits = cachedHabits,
                remoteResult = remoteResult
            ).onSuccess { habits ->
                if (remoteResult.isSuccess) {
                    habitCatalogLocalDataSource.replaceHabits(habits)
                }
            }
        }
    }

    suspend fun getHabits(
        searchInput: String,
        categoryId: String? = null,
        forceRefresh: Boolean = false
    ): Result<List<HabitInfo>> {
        val catalogResult = if (forceRefresh) {
            refreshHabitCatalog()
        } else {
            getHabitCatalogForRead()
        }

        return catalogResult.mapCatching { habits ->
            filterHabitCatalog(
                habits = habits,
                searchInput = searchInput,
                categoryId = categoryId
            )
        }
    }

    suspend fun getHabitCategories(): Result<List<HabitCategoryData>> {
        return remoteDataSource.getHabitCategories()
    }

    suspend fun getHabitIcons(): Result<List<String>> {
        return remoteDataSource.getHabitIcons()
    }

    private fun filterHabitCatalog(
        habits: List<HabitInfo>,
        searchInput: String,
        categoryId: String?
    ): List<HabitInfo> {
        val normalizedSearch = searchInput.trim().lowercase()

        return habits
            .asSequence()
            .filter { habit -> categoryId == null || habit.categoryId == categoryId }
            .filter { habit -> habit.name.lowercase().contains(normalizedSearch) }
            .toList()
    }

    fun getUserHabitsByDayFlow(day: LocalDate): Flow<List<UserHabitRecordFullInfo>> {
        return observeFullHabitRecords(
            recordsFlow = localDataSource.getUserHabitsByDateFlow(day),
            untilDate = day,
            flowName = "day flow"
        )
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
        val record = localDataSource.getHabitRecordByRecordId(habitRecordId) ?: return

        localDataSource.updateHabitCompletionByRecordId(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = isCompleted
        )
        updateFlowerHealth(record.userHabitId, isCompleted)
    }

    suspend fun updateHabitCompletionByHabitId(
        habitId: Long,
        date: LocalDate,
        isCompleted: Boolean
    ) {
        localDataSource.updateHabitCompletionByHabitId(
            habitId = habitId,
            date = date,
            isCompleted = isCompleted
        )
        updateFlowerHealth(habitId, isCompleted)
    }

    private suspend fun updateFlowerHealth(
        userHabitId: Long,
        isCompleted: Boolean
    ) {
        if (isCompleted) {
            flowerHealthRepository.updateHealthForCompletedHabit(userHabitId)
        } else {
            flowerHealthRepository.updateHealthForMissedHabit(userHabitId)
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
            remoteDataSource.savePersonalHabit(
                userId = userId,
                title = title,
                description = description,
                categoryId = categoryId,
                icon = icon
            ).onSuccess {
                refreshHabitCatalog().onFailure { error ->
                    Napier.e(
                        "Failed to refresh habits catalog after creating personal habit",
                        error,
                        tag = TAG
                    )
                }
            }
        }
    }

    fun getListOfAllUserHabitRecordsFlow(
        untilDate: LocalDate = getCurrentDate()
    ): Flow<List<UserHabitRecordFullInfo>> {
        return observeFullHabitRecords(
            recordsFlow = localDataSource.getAllUserHabitRecords(untilDate),
            untilDate = untilDate,
            flowName = "all-records flow"
        )
    }

    private fun observeFullHabitRecords(
        recordsFlow: Flow<List<UserHabitRecord>>,
        untilDate: LocalDate,
        flowName: String
    ): Flow<List<UserHabitRecordFullInfo>> {
        return combine(
            habitCatalogLocalDataSource.observeHabits(),
            recordsFlow
        ) { habitCatalog, habitRecords ->
            buildFullHabitRecords(
                habitCatalog = habitCatalog,
                habitRecords = habitRecords,
                untilDate = untilDate
            )
        }.onStart {
            loadCatalogIfEmpty(flowName)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    fun getUserHabitWithAllRecordsFlow(
        userHabitId: Long
    ): Flow<UserHabitFullInfo?> {
        return combine(
            habitCatalogLocalDataSource.observeHabits(),
            localDataSource.getAllUserHabitRecordsForHabitId(userHabitId)
        ) { habitCatalog, localHabitRecords ->
            buildUserHabitFullInfo(
                userHabitId = userHabitId,
                habitCatalog = habitCatalog,
                habitRecords = localHabitRecords
            )
        }.onStart {
            loadCatalogIfEmpty("habit details flow")
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    private suspend fun buildUserHabitFullInfo(
        userHabitId: Long,
        habitCatalog: List<HabitInfo>,
        habitRecords: List<UserHabitRecord>
    ): UserHabitFullInfo? {
        val userHabitInfo = localDataSource.getUserHabitInfo(userHabitId) ?: return null
        val habitDetails = habitCatalog.find { it.id == userHabitInfo.habitId } ?: return null
        val currentStreak = localDataSource.getHabitDayStreak(
            userHabitId = userHabitId,
            byDate = getCurrentDate()
        )

        return UserHabitFullInfo(
            userHabitId = userHabitId,
            description = habitDetails.description,
            iconUrl = habitDetails.iconUrl,
            name = habitDetails.name,
            daysStreak = currentStreak,
            records = habitRecords,
            timeOfDay = userHabitInfo.timeOfDay,
            startDate = userHabitInfo.startDate,
            days = userHabitInfo.daysOfWeek,
            reminderTime = userHabitInfo.reminderTime,
            reminderEnabled = userHabitInfo.reminderEnabled,
            endDate = userHabitInfo.endDate
        )
    }

    private suspend fun buildFullHabitRecords(
        habitCatalog: List<HabitInfo>,
        habitRecords: List<UserHabitRecord>,
        untilDate: LocalDate
    ): List<UserHabitRecordFullInfo> {
        val userHabitIds = habitRecords.map { it.userHabitId }.toSet()
        val userHabitsById = localDataSource.getAllUserHabits()
            .asSequence()
            .filter { it.id in userHabitIds }
            .associateBy { it.id }

        return buildFullHabitRecords(
            habitCatalog = habitCatalog,
            habitRecords = habitRecords,
            userHabitsById = userHabitsById,
            untilDate = untilDate
        )
    }

    private suspend fun buildFullHabitRecords(
        habitCatalog: List<HabitInfo>,
        habitRecords: List<UserHabitRecord>,
        userHabitsById: Map<Long, UserHabit>,
        untilDate: LocalDate
    ): List<UserHabitRecordFullInfo> {
        val userHabitIds = habitRecords.map { it.userHabitId }.toSet()
        val habitsByRemoteId = habitCatalog.associateBy { it.id }
        val streaksByUserHabitId = localDataSource.getHabitDayStreaks(
            userHabitIds = userHabitIds,
            byDate = untilDate
        )

        return mergeHabitRecordsWithDetails(
            habitRecords = habitRecords,
            userHabitsById = userHabitsById,
            habitsByRemoteId = habitsByRemoteId,
            streaksByUserHabitId = streaksByUserHabitId
        )
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
            runCatching {
                localDataSource.clearPastRecords(
                    userHabitId = userHabitId,
                    currentDate = getCurrentDate()
                )
            }.onSuccess { count ->
                Napier.d("Cleared $count past records for habit $userHabitId", tag = TAG)
            }.onFailure { error ->
                Napier.e("Failed to clear past records for habit $userHabitId", error, tag = TAG)
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
            remoteDataSource.deleteCustomHabit(habitId)
                .onSuccess {
                    habitCatalogLocalDataSource.deleteHabit(habitId)
                }.onFailure { error ->
                    Napier.e("Failed to delete custom habit: ${error.message}", error, tag = TAG)
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
        timeOfDay: TimeOfDay,
        startDate: LocalDate,
        endDate: LocalDate,
        selectedDays: List<DayOfWeek> = emptyList(),
        reminderEnabled: Boolean = false,
        reminderTime: LocalTime? = null
    ): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                if (hasActiveHabitInstance(habitInfo.id)) {
                    return@withContext Result.failure(
                        ActiveHabitAlreadyExistsException(habitInfo.id)
                    )
                }

                val userHabitId = localDataSource.insertUserHabit(
                    userHabit = createUserHabit(
                        habitInfo = habitInfo,
                        timeOfDay = timeOfDay,
                        startDate = startDate,
                        endDate = endDate,
                        selectedDays = selectedDays,
                        reminderEnabled = reminderEnabled,
                        reminderTime = reminderTime
                    )
                )

                Result.success(userHabitId)
            } catch (e: Exception) {
                Napier.e("Error adding user habit", e, tag = TAG)
                Result.failure(e)
            }
        }
    }

    private fun createUserHabit(
        habitInfo: HabitInfo,
        timeOfDay: TimeOfDay,
        startDate: LocalDate,
        endDate: LocalDate,
        selectedDays: List<DayOfWeek>,
        reminderEnabled: Boolean,
        reminderTime: LocalTime?
    ): UserHabit {
        return UserHabit(
            id = 0L,
            habitId = habitInfo.id,
            startDate = startDate,
            endDate = endDate,
            daysOfWeek = selectedDays.ifEmpty { DayOfWeek.entries },
            timeOfDay = timeOfDay,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
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
                ensureNotificationPermission().getOrThrow()
                val habitInfo = getHabitForReminder(habitId).getOrThrow()
                val nearestDate = getNearestReminderDate(
                    habitId = habitId,
                    reminderTime = reminderTime
                ).getOrThrow()

                notificationManager.scheduleHabitReminder(
                    habitId = habitId,
                    habitName = habitInfo.name,
                    description = habitInfo.description,
                    time = reminderTime,
                    date = nearestDate
                )
            }.onFailure {
                Napier.e("Error scheduling reminder", it, tag = TAG)
            }
        }
    }

    private suspend fun ensureNotificationPermission(): Result<Unit> {
        if (permissionsManager.hasNotificationPermission()) {
            return Result.success(Unit)
        }

        return if (permissionsManager.requestNotificationPermission()) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Notification permission denied"))
        }
    }

    private suspend fun getHabitForReminder(habitId: Long): Result<UserHabitFullInfo> {
        val habitInfo = getUserHabitWithAllRecordsFlow(habitId).first()
        return if (habitInfo == null) {
            Result.failure(Exception("Invalid habitId"))
        } else {
            Result.success(habitInfo)
        }
    }

    private suspend fun getNearestReminderDate(
        habitId: Long,
        reminderTime: LocalTime
    ): Result<LocalDate> {
        val nearestDate = getNearestDateForNotification(
            dates = getFutureDaysForHabit(habitId),
            notificationTime = reminderTime
        )

        return if (nearestDate == null) {
            Result.failure(Exception("No future available dates for such habit notification"))
        } else {
            Result.success(nearestDate)
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
                applyHabitReminderUpdate(
                    enabled = enabled,
                    reminderTime = reminderTime,
                    persistReminder = { updatedEnabled, updatedTime ->
                        localDataSource.updateHabitReminder(
                            habitId = habitId,
                            enabled = updatedEnabled,
                            reminderTime = updatedTime
                        )
                    },
                    scheduleReminder = { time ->
                        scheduleReminderForHabit(habitId, time)
                    },
                    cancelReminder = {
                        notificationManager.cancelHabitReminder(habitId)
                    }
                )
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
                val habitInfo = getHabitCatalogOrEmpty("habit details")
                    .find { it.id == userHabit.habitId }

                buildHabitNotificationDetails(
                    habitId = habitId,
                    userHabit = userHabit,
                    habitInfo = habitInfo
                )
            } catch (e: Exception) {
                Napier.e("Error getting habit details", e, tag = TAG)
                null
            }
        }
    }

    private fun buildHabitNotificationDetails(
        habitId: Long,
        userHabit: UserHabit,
        habitInfo: HabitInfo?
    ): HabitNotificationDetails {
        return HabitNotificationDetails(
            id = habitId,
            name = habitInfo?.name ?: "Habit Reminder",
            description = habitInfo?.description ?: "Time for your habit!",
            activeDays = userHabit.daysOfWeek,
            reminderEnabled = userHabit.reminderEnabled,
            reminderTime = userHabit.reminderTime
        )
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
     * Gets all user habit records for a specific time of day (non-reactive).
     *
     * @param timeOfDay The time of day to filter by
     * @return List of habit records for the specified time of day
     */
    suspend fun getHabitRecordsByTimeOfDay(timeOfDay: TimeOfDay): List<UserHabitRecordFullInfo> =
        withContext(Dispatchers.IO) {
            val currentDate = getCurrentDate()
            val records = localDataSource.getAllUserHabitRecords(currentDate).first()
            val userHabitsById = localDataSource.getAllUserHabits().associateBy { it.id }
            val filteredRecords = records.filter { record ->
                userHabitsById[record.userHabitId]?.timeOfDay == timeOfDay
            }

            buildFullHabitRecords(
                habitCatalog = getHabitCatalogOrEmpty("time-of-day query"),
                habitRecords = filteredRecords,
                userHabitsById = userHabitsById,
                untilDate = currentDate
            )
        }

    /**
     * Checks if an active instance of a habit is already added by the user.
     *
     * @param habitId The ID of the habit to check
     * @return True if any instance has records for today or a future day, false otherwise
     */
    suspend fun isHabitAlreadyAdded(habitId: String): Boolean {
        return withContext(Dispatchers.IO) {
            hasActiveHabitInstance(habitId)
        }
    }

    private suspend fun hasActiveHabitInstance(
        habitId: String,
        fromDate: LocalDate = getCurrentDate()
    ): Boolean {
        val habitInstances = localDataSource.getUserHabitsByRemoteId(habitId)
        return habitInstances.any { habit ->
            localDataSource.hasRecordsFromDate(
                userHabitId = habit.id,
                fromDate = fromDate
            )
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

internal suspend fun applyHabitReminderUpdate(
    enabled: Boolean,
    reminderTime: LocalTime?,
    persistReminder: suspend (Boolean, LocalTime?) -> Unit,
    scheduleReminder: suspend (LocalTime) -> Result<Boolean>,
    cancelReminder: suspend () -> Unit
): Boolean {
    return when {
        enabled && reminderTime != null -> {
            val scheduled = scheduleReminder(reminderTime).getOrThrow()
            if (!scheduled) {
                false
            } else {
                persistReminder(true, reminderTime)
                true
            }
        }

        else -> {
            cancelReminder()
            persistReminder(false, null)
            true
        }
    }
}

internal fun mergeHabitRecordsWithDetails(
    habitRecords: List<UserHabitRecord>,
    userHabitsById: Map<Long, UserHabit>,
    habitsByRemoteId: Map<String, HabitInfo>,
    streaksByUserHabitId: Map<Long, Int>
): List<UserHabitRecordFullInfo> {
    return habitRecords.mapNotNull { habitRecord ->
        val localHabit = userHabitsById[habitRecord.userHabitId] ?: return@mapNotNull null
        val habitDetailedInfo = habitsByRemoteId[localHabit.habitId] ?: return@mapNotNull null

        UserHabitRecordFullInfo(
            id = habitRecord.id,
            userHabitId = habitRecord.userHabitId,
            date = habitRecord.date,
            isCompleted = habitRecord.isCompleted,
            description = habitDetailedInfo.description,
            iconUrl = habitDetailedInfo.iconUrl,
            name = habitDetailedInfo.name,
            timeOfDay = localHabit.timeOfDay,
            daysStreak = streaksByUserHabitId[habitRecord.userHabitId] ?: 0
        )
    }
}

internal fun resolveCatalogRefreshResult(
    cachedHabits: List<HabitInfo>,
    remoteResult: Result<List<HabitInfo>>
): Result<List<HabitInfo>> {
    return remoteResult.recoverCatching { error ->
        cachedHabits.ifEmpty {
            throw error
        }
    }
}
