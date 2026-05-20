package com.horizondev.habitbloom.screens.garden.domain

import kotlin.math.pow
import kotlin.math.round

/**
 * Represents the current vitality status of a habit flower.
 * XP and levels are permanent; vitality only influences visual state and future XP gains.
 *
 * @property value Current vitality value ranging from 0.0 to 1.0
 * @property consecutiveMissedDays Number of currently trailing scheduled missed days
 */
data class FlowerHealth(
    val value: Float = 1.0f,
    val consecutiveMissedDays: Int = 0,
) {
    companion object {
        // Thresholds for vitality states.
        const val HEALTHY_THRESHOLD = 0.65f
        const val WILTING_THRESHOLD = 0.2f
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
