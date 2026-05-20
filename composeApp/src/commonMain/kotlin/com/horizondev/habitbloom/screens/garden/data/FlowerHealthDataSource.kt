package com.horizondev.habitbloom.screens.garden.data

import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.calculateLevelProgress
import com.horizondev.habitbloom.screens.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Data structure to hold flower vitality along with its last updated date.
 */
data class FlowerHealthRecord(
    val flowerHealth: FlowerHealth,
    val lastUpdatedDate: LocalDate?
)

/**
 * Data source for managing flower vitality calculations based on habit completion history.
 */
class FlowerHealthDataSource(
    private val database: HabitBloomDatabase,
    private val localDataSource: HabitsLocalDataSource
) {
    private val TAG = "FlowerHealthDataSource"

    /**
     * Gets the flower vitality for a specific habit by calculating it from completion history.
     *
     * @param userHabitId The habit ID
     * @return The calculated flower vitality
     */
    suspend fun getFlowerHealth(userHabitId: Long): FlowerHealth = withContext(Dispatchers.IO) {
        try {
            // Get habit info to determine start date
            val userHabit =
                localDataSource.getUserHabitInfo(userHabitId) ?: return@withContext FlowerHealth()
            val startDate = userHabit.startDate
            val currentDate = getCurrentDate()

            // Get all records from start date to now
            val records = localDataSource.getUserHabitRecordsInDateRange(
                userHabitId = userHabitId,
                startDate = startDate,
                endDate = currentDate
            ).sortedBy { it.date }

            val progress = calculateLevelProgress(
                records = records,
                daysPerWeek = userHabit.daysOfWeek.size
            )

            return@withContext FlowerHealth(
                value = progress.vitality,
                consecutiveMissedDays = progress.currentMissedDays
            )
        } catch (e: Exception) {
            Napier.e("Error calculating flower vitality", e, tag = TAG)
            return@withContext FlowerHealth()
        }
    }

    /**
     * Calculates the flower vitality for a specific habit with the last update date
     * This is used for batch processing historical records
     *
     * @param userHabitId The habit ID
     * @return FlowerHealthRecord containing vitality and last update date
     */
    suspend fun getFlowerHealthWithLastUpdatedDate(userHabitId: Long): FlowerHealthRecord? =
        withContext(Dispatchers.IO) {
            try {
                val flowerHealth = getFlowerHealth(userHabitId)
                return@withContext FlowerHealthRecord(
                    flowerHealth = flowerHealth,
                    lastUpdatedDate = getCurrentDate()
                )
            } catch (e: Exception) {
                Napier.e("Error getting flower vitality with date", e, tag = TAG)
                return@withContext null
            }
        }

    /**
     * Observes the flower vitality for a specific habit as a Flow.
     *
     * @param userHabitId The habit ID
     * @return Flow of FlowerHealth for the habit
     */
    fun observeFlowerHealth(userHabitId: Long): Flow<FlowerHealth> {
        // Create a flow of all records for this habit
        return localDataSource.getAllUserHabitRecordsForHabitId(userHabitId)
            .map { records ->
                val today = getCurrentDate()
                val userHabit = localDataSource.getUserHabitInfo(userHabitId)
                val progress = calculateLevelProgress(
                    records = records
                        .sortedBy { it.date }
                        .filter { it.date <= today },
                    daysPerWeek = userHabit?.daysOfWeek?.size ?: 7
                )
                FlowerHealth(
                    value = progress.vitality,
                    consecutiveMissedDays = progress.currentMissedDays
                )
            }
    }

    /**
     * Updates the flower vitality when a habit is completed.
     * With the runtime calculation approach, this method doesn't need to do anything
     * since vitality is calculated on demand.
     *
     * @param userHabitId The habit ID
     * @return The updated flower vitality
     */
    suspend fun updateHealthForCompletedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            // Vitality is calculated on demand now, just return the current vitality
            return@withContext getFlowerHealth(userHabitId)
        }

    /**
     * Updates the flower vitality when a habit is missed.
     * With the runtime calculation approach, this method doesn't need to do anything
     * since vitality is calculated on demand.
     *
     * @param userHabitId The habit ID
     * @return The updated flower vitality
     */
    suspend fun updateHealthForMissedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            // Vitality is calculated on demand now, just return the current vitality
            return@withContext getFlowerHealth(userHabitId)
        }

    /**
     * Updates the flower vitality with a pre-computed value.
     * With the runtime calculation approach, this method doesn't need to do anything.
     */
    suspend fun updateFlowerHealth(
        userHabitId: Long,
        health: FlowerHealth,
        updateDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        // No-op in runtime calculation approach
    }

    /**
     * Updates only the last updated date for a flower vitality record.
     * With the runtime calculation approach, this method doesn't need to do anything.
     */
    suspend fun updateLastUpdatedDate(
        userHabitId: Long,
        updateDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        // No-op in runtime calculation approach
    }

    suspend fun deleteAllFlowerHealth() = withContext(Dispatchers.IO) {
        database.flowerHealthEntityQueries.deleteAllFlowerHealth()
    }

}
