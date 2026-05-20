package com.horizondev.habitbloom.screens.garden.domain

import androidx.compose.runtime.Immutable
import kotlin.math.pow
import kotlin.math.round

@Immutable
data class FlowerHealth(
    val value: Float = 1.0f,
    val consecutiveMissedDays: Int = 0,
) {
    companion object {
        const val HEALTHY_THRESHOLD = 0.65f
        const val WILTING_THRESHOLD = 0.2f
    }

    val isWilting: Boolean
        get() = value < HEALTHY_THRESHOLD

    val isCritical: Boolean
        get() = value < WILTING_THRESHOLD
}

fun Float.roundToDecimal(decimals: Int = 1): Float {
    val factor = 10.0f.pow(decimals)
    return round(this * factor) / factor
}
