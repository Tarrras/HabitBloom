package com.horizondev.habitbloom.screens.garden.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.horizondev.habitbloom.database.HabitBloomDatabase
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.utils.getCurrentDate
import database.FlowerHealthEntity
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
 * Data source for managing flower health information in the database.
 */
class FlowerHealthDataSource(
    database: HabitBloomDatabase
) {
    private val flowerHealthQueries = database.flowerHealthEntityQueries

    /**
     * Gets the flower health for a specific habit.
     * If no health record exists, creates a new one with default values.
     *
     * @param userHabitId The habit ID
     * @return The flower health for the habit
     */
    suspend fun getFlowerHealth(userHabitId: Long): FlowerHealth = withContext(Dispatchers.IO) {
        val entity =
            flowerHealthQueries.selectFlowerHealthByUserHabitId(userHabitId).executeAsOneOrNull()

        if (entity == null) {
            // Create default health if not exists
            val defaultHealth = FlowerHealth()
            insertOrUpdateFlowerHealth(userHabitId, defaultHealth)
            return@withContext defaultHealth
        }

        return@withContext entity.toFlowerHealth()
    }

    /**
     * Gets the flower health along with the last time it was updated.
     *
     * @param userHabitId The habit ID
     * @return FlowerHealthRecord containing the health and last update date, or null if not found
     */
    suspend fun getFlowerHealthWithLastUpdatedDate(userHabitId: Long): FlowerHealthRecord? =
        withContext(Dispatchers.IO) {
            val entity = flowerHealthQueries.selectFlowerHealthByUserHabitId(userHabitId)
                .executeAsOneOrNull() ?: return@withContext null

            return@withContext FlowerHealthRecord(
                flowerHealth = entity.toFlowerHealth(),
                lastUpdatedDate = runCatching { LocalDate.parse(entity.lastUpdatedDate) }.getOrNull()
            )
        }

    /**
     * Observes the flower health for a specific habit as a Flow.
     *
     * @param userHabitId The habit ID
     * @return Flow of FlowerHealth for the habit
     */
    fun observeFlowerHealth(userHabitId: Long): Flow<FlowerHealth> {
        return flowerHealthQueries.selectFlowerHealthByUserHabitId(userHabitId)
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toFlowerHealth() }
    }

    /**
     * Updates the flower health when a habit is completed.
     *
     * @param userHabitId The habit ID
     * @return The updated flower health
     */
    suspend fun updateHealthForCompletedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            val currentHealth = getFlowerHealth(userHabitId)
            val updatedHealth = currentHealth.habitCompleted()
            insertOrUpdateFlowerHealth(userHabitId, updatedHealth)
            return@withContext updatedHealth
        }

    /**
     * Updates the flower health when a habit is missed.
     *
     * @param userHabitId The habit ID
     * @return The updated flower health
     */
    suspend fun updateHealthForMissedHabit(userHabitId: Long): FlowerHealth =
        withContext(Dispatchers.IO) {
            val currentHealth = getFlowerHealth(userHabitId)
            val updatedHealth = currentHealth.habitMissed()
            insertOrUpdateFlowerHealth(userHabitId, updatedHealth)
            return@withContext updatedHealth
        }

    /**
     * Directly updates the flower health with a pre-computed value.
     * This is useful when batch processing missed days.
     *
     * @param userHabitId The habit ID
     * @param health The new flower health
     * @param updateDate The date to mark as the last update date
     */
    suspend fun updateFlowerHealth(
        userHabitId: Long,
        health: FlowerHealth,
        updateDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        insertOrUpdateFlowerHealth(userHabitId, health, updateDate)
    }

    /**
     * Inserts or updates flower health in the database.
     *
     * @param userHabitId The habit ID
     * @param health The flower health to save
     * @param updateDate The date to mark as the last update date
     */
    private suspend fun insertOrUpdateFlowerHealth(
        userHabitId: Long,
        health: FlowerHealth,
        updateDate: LocalDate = getCurrentDate()
    ) = withContext(Dispatchers.IO) {
        flowerHealthQueries.insertOrReplaceFlowerHealth(
            userHabitId = userHabitId,
            healthValue = health.value.toDouble(),
            consecutiveMissedDays = health.consecutiveMissedDays.toLong(),
            lastUpdatedDate = updateDate.toString()
        )
    }

    /**
     * Deletes the flower health record for a specific habit.
     *
     * @param userHabitId The habit ID
     */
    suspend fun deleteFlowerHealth(userHabitId: Long) = withContext(Dispatchers.IO) {
        flowerHealthQueries.deleteFlowerHealthByUserHabitId(userHabitId)
    }
}

/**
 * Extension function to convert database entity to domain model.
 */
private fun FlowerHealthEntity.toFlowerHealth(): FlowerHealth {
    return FlowerHealth(
        value = healthValue.toFloat(),
        consecutiveMissedDays = consecutiveMissedDays.toInt()
    )
}