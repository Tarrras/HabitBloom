package com.horizondev.habitbloom.screens.garden.domain

import kotlin.math.pow
import kotlin.math.round

/**
 * Represents the health status of a habit flower.
 * Health is a buffer mechanism that allows flowers to survive missed days.
 *
 * @property value Current health value ranging from 0.0 (dead) to 1.0 (fully healthy)
 * @property consecutiveMissedDays Number of consecutive days the habit has been missed
 * @property isWilting Whether the flower is in a wilting state (health below threshold)
 */
data class FlowerHealth(
    val value: Float = 1.0f,
    val consecutiveMissedDays: Int = 0,
) {
    companion object {
        // Thresholds for health states
        const val HEALTHY_THRESHOLD = 0.7f
        const val WILTING_THRESHOLD = 0.3f

        // Health penalties for missed days
        const val FIRST_MISS_PENALTY = 0.3f
        const val SECOND_MISS_PENALTY = 0.2f
        const val ADDITIONAL_MISS_PENALTY = 0.15f

        // Health recovery for completed days - set equal to first miss penalty for balance
        const val COMPLETION_RECOVERY = 0.2f
    }

    /**
     * Determines if the flower is currently wilting (showing visual signs of low health)
     */
    val isWilting: Boolean
        get() = value < HEALTHY_THRESHOLD

    /**
     * Determines if the flower is in critical health state (close to regression)
     */
    val isCritical: Boolean
        get() = value < WILTING_THRESHOLD

    /**
     * Updates health when a habit is completed.
     *
     * @return Updated FlowerHealth instance
     */
    fun habitCompleted(): FlowerHealth {
        // Reset consecutive missed days and increase health
        val newHealth = (value + COMPLETION_RECOVERY).coerceAtMost(1.0f)
        // Round to prevent floating point precision issues
        val roundedHealth = newHealth.roundToDecimal(1)
        return copy(value = roundedHealth, consecutiveMissedDays = 0)
    }

    /**
     * Updates health when a habit is missed.
     *
     * @return Updated FlowerHealth instance
     */
    fun habitMissed(): FlowerHealth {
        val newConsecutiveMissedDays = consecutiveMissedDays + 1

        // Calculate penalty based on consecutive misses
        val penalty = when (newConsecutiveMissedDays) {
            1 -> FIRST_MISS_PENALTY
            2 -> SECOND_MISS_PENALTY
            else -> ADDITIONAL_MISS_PENALTY
        }

        val newHealth = (value - penalty).coerceAtLeast(0.0f)
        // Round to prevent floating point precision issues
        val roundedHealth = newHealth.roundToDecimal(1)
        return copy(value = roundedHealth, consecutiveMissedDays = newConsecutiveMissedDays)
    }

    /**
     * Determines if the flower should regress to a previous growth stage.
     *
     * @return True if the flower should regress, false otherwise
     */
    fun shouldRegress(): Boolean {
        return consecutiveMissedDays >= 3 && isCritical
    }
}

/**
 * Rounds a float value to the specified number of decimal places to avoid floating point precision issues.
 *
 * @param decimals The number of decimal places to round to
 * @return The rounded value
 */
fun Float.roundToDecimal(decimals: Int = 1): Float {
    val factor = 10.0f.pow(decimals)
    return round(this * factor) / factor
}
