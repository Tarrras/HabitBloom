package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.afternoon_flower_stage_1
import habitbloom.composeapp.generated.resources.afternoon_flower_stage_2
import habitbloom.composeapp.generated.resources.afternoon_flower_stage_3
import habitbloom.composeapp.generated.resources.afternoon_flower_stage_4
import habitbloom.composeapp.generated.resources.afternoon_flower_stage_5
import habitbloom.composeapp.generated.resources.evening_flower_stage_1
import habitbloom.composeapp.generated.resources.evening_flower_stage_2
import habitbloom.composeapp.generated.resources.evening_flower_stage_3
import habitbloom.composeapp.generated.resources.evening_flower_stage_4
import habitbloom.composeapp.generated.resources.evening_flower_stage_5
import habitbloom.composeapp.generated.resources.morning_flower_stage_1
import habitbloom.composeapp.generated.resources.morning_flower_stage_2
import habitbloom.composeapp.generated.resources.morning_flower_stage_3
import habitbloom.composeapp.generated.resources.morning_flower_stage_4
import habitbloom.composeapp.generated.resources.morning_flower_stage_5
import org.jetbrains.compose.resources.DrawableResource

enum class FlowerType {
    MORNING,
    AFTERNOON,
    EVENING;

    fun getFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (this) {
            MORNING -> getMorningFlowerResource(growthStage)
            AFTERNOON -> getAfternoonFlowerResource(growthStage)
            EVENING -> getEveningFlowerResource(growthStage)
        }
    }

    private fun getMorningFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (growthStage) {
            FlowerGrowthStage.SEED -> Res.drawable.morning_flower_stage_1
            FlowerGrowthStage.SPROUT -> Res.drawable.morning_flower_stage_2
            FlowerGrowthStage.BUSH -> Res.drawable.morning_flower_stage_3
            FlowerGrowthStage.BUD -> Res.drawable.morning_flower_stage_4
            FlowerGrowthStage.BLOOM -> Res.drawable.morning_flower_stage_5
        }
    }

    private fun getAfternoonFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (growthStage) {
            FlowerGrowthStage.SEED -> Res.drawable.afternoon_flower_stage_1
            FlowerGrowthStage.SPROUT -> Res.drawable.afternoon_flower_stage_2
            FlowerGrowthStage.BUSH -> Res.drawable.afternoon_flower_stage_3
            FlowerGrowthStage.BUD -> Res.drawable.afternoon_flower_stage_4
            FlowerGrowthStage.BLOOM -> Res.drawable.afternoon_flower_stage_5
        }
    }

    private fun getEveningFlowerResource(growthStage: FlowerGrowthStage): DrawableResource {
        return when (growthStage) {
            FlowerGrowthStage.SEED -> Res.drawable.evening_flower_stage_1
            FlowerGrowthStage.SPROUT -> Res.drawable.evening_flower_stage_2
            FlowerGrowthStage.BUSH -> Res.drawable.evening_flower_stage_3
            FlowerGrowthStage.BUD -> Res.drawable.evening_flower_stage_4
            FlowerGrowthStage.BLOOM -> Res.drawable.evening_flower_stage_5
        }
    }

    companion object {
        fun fromTimeOfDay(timeOfDay: TimeOfDay): FlowerType {
            return when (timeOfDay) {
                TimeOfDay.Morning -> MORNING
                TimeOfDay.Afternoon -> AFTERNOON
                TimeOfDay.Evening -> EVENING
            }
        }
    }
} 