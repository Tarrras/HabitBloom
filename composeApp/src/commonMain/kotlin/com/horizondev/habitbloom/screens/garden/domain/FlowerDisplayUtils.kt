package com.horizondev.habitbloom.screens.garden.domain

/**
 * Utility functions for displaying flowers in the UI.
 */
object FlowerDisplayUtils {
    /**
     * Determines the growth stage to display based on the actual growth stage and flower vitality.
     * When vitality is critical (value < WILTING_THRESHOLD), the flower regresses visually by one stage.
     * When vitality is low but not critical, only visual effects apply; the stage remains.
     *
     * @param actualGrowthStage The current or maximum growth stage based on streak
     * @param flowerHealth The health status of the flower
     * @return The growth stage to display visually
     */
    fun determineDisplayGrowthStage(
        actualGrowthStage: FlowerGrowthStage,
        flowerHealth: FlowerHealth
    ): FlowerGrowthStage {
        // No stage regression; health influences only visual effects elsewhere
        return actualGrowthStage
    }
} 