package com.horizondev.habitbloom.screens.garden.domain

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Enum defining the different growth stages of a habit flower.
 * Each stage corresponds to a specific streak range.
 *
 * @property streakThreshold The minimum streak required to reach this stage
 */
enum class FlowerGrowthStage(
    val streakThreshold: Int
) {
    /**
     * Initial stage - just a seed in the soil
     */
    SEED(0),

    /**
     * Second stage - small sprout emerges from soil
     */
    SPROUT(3),

    /**
     * Third stage - small plant with leaves
     */
    BUSH(7),

    /**
     * Fourth stage - plant develops a flower bud
     */
    BUD(14),

    /**
     * Fifth stage - flower begins to bloom
     */
    BLOOM(30);

    companion object {
        /**
         * Determines the flower growth stage based on the current streak.
         *
         * @param streak The current habit streak
         * @return The appropriate flower growth stage
         */
        fun fromStreak(streak: Int): FlowerGrowthStage {
            return entries.toTypedArray().findLast { stage ->
                streak >= stage.streakThreshold
            } ?: SEED
        }

        /**
         * Calculates how many more streak days are needed to reach the next stage.
         *
         * @param currentStreak The current habit streak
         * @return Number of days needed to reach next stage (0 if at max stage)
         */
        fun streakToNextStage(currentStreak: Int): Int {
            val currentStage = fromStreak(currentStreak)
            val values = entries.toTypedArray()

            // Find the next stage
            val nextStageIndex = currentStage.ordinal + 1

            // If there's no next stage (already at max), return 0
            if (nextStageIndex >= values.size) {
                return 0
            }

            val nextStage = values[nextStageIndex]

            // Calculate days needed to reach next stage
            return nextStage.streakThreshold - currentStreak
        }
    }
}

fun FlowerGrowthStage.iconWidth(): Dp {
    return when (this) {
        FlowerGrowthStage.SEED -> 60.dp
        FlowerGrowthStage.SPROUT -> 70.dp
        FlowerGrowthStage.BUSH -> 80.dp
        FlowerGrowthStage.BUD -> 90.dp
        FlowerGrowthStage.BLOOM -> 95.dp
    }
}