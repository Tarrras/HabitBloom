package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.garden.data.FlowerHealthDataSource
import com.horizondev.habitbloom.utils.getCurrentDate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository responsible for managing flower health for habits.
 * This repository encapsulates all interactions with FlowerHealthDataSource
 * and provides a clean API for other components to interact with flower health data.
 */
class FlowerHealthRepository(
    private val flowerHealthDataSource: FlowerHealthDataSource
) {
    private val TAG = "FlowerHealthRepository"

    /**
     * Gets the current flower health for a habit directly (non-reactive).
     *
     * @param habitId The ID of the habit
     * @return The current FlowerHealth
     */
    suspend fun getFlowerHealth(habitId: Long): FlowerHealth = withContext(Dispatchers.IO) {
        flowerHealthDataSource.getFlowerHealth(habitId)
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

    /**
     * Updates flower health when a habit is completed.
     *
     * @param habitId The ID of the habit
     * @return The updated flower health
     */
    suspend fun updateHealthForCompletedHabit(habitId: Long): FlowerHealth {
        return flowerHealthDataSource.updateHealthForCompletedHabit(habitId)
    }

    /**
     * Updates flower health when a habit is missed.
     *
     * @param habitId The ID of the habit
     * @return The updated flower health
     */
    suspend fun updateHealthForMissedHabit(habitId: Long): FlowerHealth {
        return flowerHealthDataSource.updateHealthForMissedHabit(habitId)
    }

    /**
     * Updates flower health for all habits that haven't been updated today.
     * This method now only tracks the last check date since health is calculated on demand.
     * This should be called when the app starts to account for days when the user didn't open the app.
     */
    suspend fun updateFlowerHealthForMissedDays() {
        withContext(Dispatchers.IO) {
            try {
                // No need to process records since health is calculated on demand now
                // We can track the last check date in settings if needed

                // Just log that we checked today for debugging purposes
                Napier.d("Checked flower health for missed days on ${getCurrentDate()}", tag = TAG)
            } catch (e: Exception) {
                Napier.e("Error checking flower health for missed days", e, tag = TAG)
            }
        }
    }
} 