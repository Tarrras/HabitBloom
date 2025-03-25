package com.horizondev.habitbloom.screens.flowerdetail.domain

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.evening_flower_stage_1
import habitbloom.composeapp.generated.resources.evening_flower_stage_2
import habitbloom.composeapp.generated.resources.evening_flower_stage_3
import habitbloom.composeapp.generated.resources.evening_flower_stage_4
import habitbloom.composeapp.generated.resources.evening_flower_stage_5
import org.jetbrains.compose.resources.DrawableResource

/**
 * Enum defining the different types of flowers based on time of day.
 * Each time of day has different flower visuals.
 */
enum class FlowerType {
    MORNING,
    AFTERNOON,
    EVENING;

    /**
     * Gets the appropriate drawable resource for this flower type at the given growth stage.
     *
     * @param growthStage The current growth stage of the flower
     * @return The drawable resource for the flower
     */
    fun getFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (this) {
            MORNING -> getMorningFlowerResource(growthStage)
            AFTERNOON -> getAfternoonFlowerResource(growthStage)
            EVENING -> getEveningFlowerResource(growthStage)
        }
    }

    /**
     * Gets the drawable resource for morning flowers at the given growth stage.
     *
     * @param growthStage The current growth stage of the flower
     * @return The drawable resource for the morning flower
     */
    private fun getMorningFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (growthStage) {
            FlowerGrowthStage.SEED -> Res.drawable.evening_flower_stage_1
            FlowerGrowthStage.SPROUT -> Res.drawable.evening_flower_stage_2
            FlowerGrowthStage.BUSH -> Res.drawable.evening_flower_stage_3
            FlowerGrowthStage.BUD -> Res.drawable.evening_flower_stage_4
            FlowerGrowthStage.BLOOM -> Res.drawable.evening_flower_stage_5
        }
    }

    /**
     * Gets the drawable resource for afternoon flowers at the given growth stage.
     *
     * @param growthStage The current growth stage of the flower
     * @return The drawable resource for the afternoon flower
     */
    private fun getAfternoonFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        // For now, use same resources as morning
        return getMorningFlowerResource(growthStage)
    }

    /**
     * Gets the drawable resource for evening flowers at the given growth stage.
     *
     * @param growthStage The current growth stage of the flower
     * @return The drawable resource for the evening flower
     */
    private fun getEveningFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        // For now, use same resources as morning
        return getMorningFlowerResource(growthStage)
    }

    companion object {
        /**
         * Creates a flower type based on time of day.
         *
         * @param timeOfDay The time of day
         * @return The appropriate flower type
         */
        fun fromTimeOfDay(timeOfDay: TimeOfDay): FlowerType {
            return when (timeOfDay) {
                TimeOfDay.Morning -> MORNING
                TimeOfDay.Afternoon -> AFTERNOON
                TimeOfDay.Evening -> EVENING
            }
        }
    }
} 