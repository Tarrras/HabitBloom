package com.horizondev.habitbloom.habits.domain

import com.horizondev.habitbloom.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.habits.data.remote.SupabaseStorageService
import com.horizondev.habitbloom.habits.data.remote.toDomainModel
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.domain.models.UserHabit
import com.horizondev.habitbloom.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.profile.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.calculateCompletedRepeats
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
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: HabitsLocalDataSource,
    private val storageService: SupabaseStorageService
) {
    private val TAG = "HabitsRepository"
    private val remoteHabits = MutableStateFlow<List<HabitInfo>>(emptyList())

    suspend fun initData(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            //remoteDataSource.pushHabitsToFirestore()
            // Initialize Supabase storage bucket
            storageService.initializeBucket()
            
            getAllHabits().onSuccess { habits ->
                remoteHabits.update { habits }
            }.map { true }
        }
    }

    private suspend fun getAllHabits(): Result<List<HabitInfo>> {
        return withContext(Dispatchers.IO) {
            Napier.d("Fetching network habits...", tag = TAG)
            val userId = profileRemoteDataSource.getUser().getOrNull()?.id
            remoteDataSource.getHabits(userId)
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

    suspend fun updateHabitCompletion(habitRecordId: Long, date: LocalDate, isCompleted: Boolean) {
        localDataSource.updateHabitCompletion(
            habitRecordId = habitRecordId,
            date = date,
            isCompleted = isCompleted
        )
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
            )
        }
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
                shortInfo = habitDetailedInfo.shortInfo,
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
                    basicRepeats = userHabitInfo.repeats,
                    habitDays = userHabitInfo.daysOfWeek
                )
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
                shortInfo = habitDetailedInfo.shortInfo,
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
                Result.success(count)
            } catch (e: Exception) {
                Napier.e("Failed to clear past records for habit $userHabitId", e, tag = TAG)
                Result.failure(e)
            }
        }
    }
}