package com.horizondev.habitbloom.screens.garden.domain

import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.evening_flower_stage_1
import habitbloom.composeapp.generated.resources.evening_flower_stage_2
import habitbloom.composeapp.generated.resources.evening_flower_stage_3
import habitbloom.composeapp.generated.resources.evening_flower_stage_4
import habitbloom.composeapp.generated.resources.evening_flower_stage_5
import habitbloom.composeapp.generated.resources.evening_flower_stage_6
import org.jetbrains.compose.resources.DrawableResource

/**
 * Represents the different stages of a habit's blooming cycle based on streak length
 */
enum class BloomingStage {
    SEED,         // SEED - habit is created, no streak
    SPROUT,       // SPROUT - streak is 1
    PLANT,        // PLANT - streak is 3
    BUD,          // BUD - streak is 7
    SMALL_FLOWER, // SMALL_FLOWER - streak is 7-14
    LARGE_FLOWER; // LARGE_FLOWER - streak is 14+

    /**
     * Returns the drawable resource for the evening flower at this blooming stage
     */
    fun getEveningFlowerResource(): DrawableResource {
        return when (this) {
            SEED -> Res.drawable.evening_flower_stage_1
            SPROUT -> Res.drawable.evening_flower_stage_2
            PLANT -> Res.drawable.evening_flower_stage_3
            BUD -> Res.drawable.evening_flower_stage_4
            SMALL_FLOWER -> Res.drawable.evening_flower_stage_5
            LARGE_FLOWER -> Res.drawable.evening_flower_stage_6
        }
    }

    /**
     * Determines the blooming stage based on the habit streak
     */
    companion object {
        fun fromStreak(streak: Int): BloomingStage {
            return when (streak) {
                0 -> SEED
                1, 2 -> SPROUT
                in 3..6 -> PLANT
                7 -> BUD
                in 8..13 -> SMALL_FLOWER
                else -> LARGE_FLOWER
            }
        }
    }
} 