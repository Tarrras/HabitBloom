package com.horizondev.habitbloom.screens.garden.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.flower_stage_bloom
import habitbloom.composeapp.generated.resources.flower_stage_bud
import habitbloom.composeapp.generated.resources.flower_stage_bush
import habitbloom.composeapp.generated.resources.flower_stage_seed
import habitbloom.composeapp.generated.resources.flower_stage_sprout
import org.jetbrains.compose.resources.stringResource

/**
 * Enum defining the different growth stages of a habit flower.
 * XP level determines the current stage.
 */
enum class FlowerGrowthStage {
    /**
     * Initial stage - just a seed in the soil
     */
    SEED,

    /**
     * Second stage - small sprout emerges from soil
     */
    SPROUT,

    /**
     * Third stage - small plant with leaves
     */
    BUSH,

    /**
     * Fourth stage - plant develops a flower bud
     */
    BUD,

    /**
     * Fifth stage - flower begins to bloom
     */
    BLOOM;
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

/**
 * Returns the string resource ID for the given [FlowerGrowthStage].
 */
@Composable
fun FlowerGrowthStage.getTitle(): String {
    return when (this) {
        FlowerGrowthStage.SEED -> stringResource(Res.string.flower_stage_seed)
        FlowerGrowthStage.SPROUT -> stringResource(Res.string.flower_stage_sprout)
        FlowerGrowthStage.BUSH -> stringResource(Res.string.flower_stage_bush)
        FlowerGrowthStage.BUD -> stringResource(Res.string.flower_stage_bud)
        FlowerGrowthStage.BLOOM -> stringResource(Res.string.flower_stage_bloom)
    }
}
