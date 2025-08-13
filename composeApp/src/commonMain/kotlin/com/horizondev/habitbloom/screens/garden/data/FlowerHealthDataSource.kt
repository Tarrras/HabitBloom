package com.horizondev.habitbloom.screens.garden.data

import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.roundToDecimal
import com.horizondev.habitbloom.screens.habits.data.database.HabitsLocalDataSource
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Data structure to hold flower health along with its last updated date.
 */
data class FlowerHealthRecord(
    val flowerHealth: FlowerHealth,
    val lastUpdatedDate: LocalDate?
)

/**
 * Data source for managing flower health calculations based on habit completion history.
 */
class FlowerHealthDataSource(
    database: HabitBloomDatabase,
    private val localDataSource: HabitsLocalDataSource
) {
    private val TAG = "FlowerHealthDataSource"

    /**
     * Gets the flower health for a specific habit by calculating it from completion history.
     *
     * @param userHabitId The habit ID
     * @return The calculated flower health
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

            // EMA vitality based on scheduled records
            val alpha = selectAlpha(userHabit.daysOfWeek.size)
            var vitality = 0.6f
            var consecutiveMissedDays = 0

            for (record in records) {
                val x = if (record.isCompleted) 1f else 0f
                vitality = alpha * x + (1 - alpha) * vitality
                if (record.isCompleted) {
                    consecutiveMissedDays = 0
                } else {
                    consecutiveMissedDays++
                }
            }

            // If brand-new (no records), show full vitality; if all completed so far, also full
            vitality = when {
                records.isEmpty() -> 1.0f
                records.all { it.isCompleted } -> 1.0f
                else -> vitality
            }.roundToDecimal(2)

            return@withContext FlowerHealth(
                value = vitality,
                consecutiveMissedDays = consecutiveMissedDays
            )
        } catch (e: Exception) {
            Napier.e("Error calculating flower health", e, tag = TAG)
            return@withContext FlowerHealth()
        }
    }

    /**
     * Calculates the flower health for a specific habit with the last update date
     * This is used for batch processing historical records
     *
     * @param userHabitId The habit ID
     * @return FlowerHealthRecord containing the health and last update date
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
                Napier.e("Error getting flower health with date", e, tag = TAG)
                return@withContext null
            }
        }

    /**
     * Observes the flower health for a specific habit as a Flow.
     *
     * @param userHabitId The habit ID
     * @return Flow of FlowerHealth for the habit
     */
    fun observeFlowerHealth(userHabitId: Long): Flow<FlowerHealth> {
        // Create a flow of all records for this habit
        return localDataSource.getAllUserHabitRecordsForHabitId(userHabitId)
            .map { records -> calculateFlowerHealth(userHabitId, records) }
    }

    /**
     * Helper method to calculate flower health from a list of records
     * Used by the observeFlowerHealth flow
     */
    private suspend fun calculateFlowerHealth(
        userHabitId: Long,
        records: List<UserHabitRecord>
    ): FlowerHealth {
        val today = getCurrentDate()
        val filteredRecords = records
            .sortedBy { it.date }
            .filter { it.date <= today }

        val userHabit = localDataSource.getUserHabitInfo(userHabitId)
        val alpha = selectAlpha(userHabit?.daysOfWeek?.size ?: 7)

        var vitality = 0.6f
        var consecutiveMissedDays = 0

        for (record in filteredRecords) {
            val x = if (record.isCompleted) 1f else 0f
            vitality = alpha * x + (1 - alpha) * vitality
            if (record.isCompleted) {
                consecutiveMissedDays = 0
            } else {
                consecutiveMissedDays++
            }
        }

        vitality = vitality.roundToDecimal(2)

        return FlowerHealth(value = vitality, consecutiveMissedDays = consecutiveMissedDays)
    }

    /**
     * Updates the flower health when a habit is completed.
     * With the runtime calculation approach, this method doesn't need to do anything
     * since health is calculated on demand.
     *
     * @param userHabitId The habit ID
     * @return The updated flower health
     */
    suspend fun updateHealthForCompletedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            // Health is calculated on demand now, just return the current health
            return@withContext getFlowerHealth(userHabitId)
        }

    /**
     * Updates the flower health when a habit is missed.
     * With the runtime calculation approach, this method doesn't need to do anything
     * since health is calculated on demand.
     *
     * @param userHabitId The habit ID
     * @return The updated flower health
     */
    suspend fun updateHealthForMissedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            // Health is calculated on demand now, just return the current health
            return@withContext getFlowerHealth(userHabitId)
        }

    /**
     * Updates the flower health with a pre-computed value.
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
     * Updates only the last updated date for a flower health record.
     * With the runtime calculation approach, this method doesn't need to do anything.
     */
    suspend fun updateLastUpdatedDate(
        userHabitId: Long,
        updateDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        // No-op in runtime calculation approach
    }

    private fun selectAlpha(daysPerWeek: Int): Float {
        return when {
            daysPerWeek >= 5 -> 0.15f
            daysPerWeek >= 3 -> 0.10f
            else -> 0.08f
        }
    }
}