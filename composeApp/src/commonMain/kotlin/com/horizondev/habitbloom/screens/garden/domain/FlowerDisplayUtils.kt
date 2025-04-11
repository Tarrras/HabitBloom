package com.horizondev.habitbloom.screens.garden.domain

/**
 * Utility functions for displaying flowers in the UI.
 */
object FlowerDisplayUtils {
    /**
     * Determines the growth stage to display based on the actual growth stage and flower health.
     * When health is critical, the flower will be displayed at a lower growth stage.
     * When health is wilting but not critical, visual effects will be applied but stage remains.
     *
     * @param actualGrowthStage The current or maximum growth stage based on streak
     * @param flowerHealth The health status of the flower
     * @return The growth stage to display visually
     */
    fun determineDisplayGrowthStage(
        actualGrowthStage: FlowerGrowthStage,
        flowerHealth: FlowerHealth
    ): FlowerGrowthStage {
        // When health is critical (below 0.3), reduce the stage by 1
        if (flowerHealth.isCritical) {
            val currentIndex = actualGrowthStage.ordinal
            // Ensure we don't go below SEED stage
            return if (currentIndex > 0) {
                FlowerGrowthStage.entries[currentIndex - 1]
            } else {
                FlowerGrowthStage.SEED
            }
        }

        // If health is wilting (below 0.7) but not critical, keep the same stage
        // Visual effects (desaturation and alpha) will be applied by other code
        return actualGrowthStage
    }
} 