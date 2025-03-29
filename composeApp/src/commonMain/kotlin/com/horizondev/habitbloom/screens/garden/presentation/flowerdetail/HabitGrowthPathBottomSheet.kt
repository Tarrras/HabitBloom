package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.charts.BloomLinearProgressIndicator
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.bloom_stage_continued
import habitbloom.composeapp.generated.resources.current_streak_info
import habitbloom.composeapp.generated.resources.days_to_next_stage
import habitbloom.composeapp.generated.resources.flower_health_system
import habitbloom.composeapp.generated.resources.full_growth_path
import habitbloom.composeapp.generated.resources.growth_stages_explained
import habitbloom.composeapp.generated.resources.growth_system_explanation
import habitbloom.composeapp.generated.resources.health_description
import habitbloom.composeapp.generated.resources.health_rule_consecutive_misses
import habitbloom.composeapp.generated.resources.health_rule_missing_days
import habitbloom.composeapp.generated.resources.health_rule_recovery
import habitbloom.composeapp.generated.resources.health_status_critical
import habitbloom.composeapp.generated.resources.health_status_healthy
import habitbloom.composeapp.generated.resources.health_status_wilting
import habitbloom.composeapp.generated.resources.stage_description_bloom
import habitbloom.composeapp.generated.resources.stage_description_bud
import habitbloom.composeapp.generated.resources.stage_description_bush
import habitbloom.composeapp.generated.resources.stage_description_seed
import habitbloom.composeapp.generated.resources.stage_description_sprout
import habitbloom.composeapp.generated.resources.stage_progress_info
import habitbloom.composeapp.generated.resources.stage_threshold_next
import habitbloom.composeapp.generated.resources.stage_threshold_started
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * A bottom sheet that displays the full growth path of a habit with an appealing visual design.
 *
 * @param currentStage The current growth stage of the habit.
 * @param streaksToNextStage Days needed to reach the next stage.
 * @param onDismissRequest Callback invoked when the bottom sheet is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitGrowthPathBottomSheet(
    currentStage: FlowerGrowthStage,
    flowerType: FlowerType,
    streaksToNextStage: Int,
    currentStreak: Int,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // List of all growth stages in order
    val allStages = remember { FlowerGrowthStage.entries }

    // Calculate current progress within the overall growth path
    val currentStageIndex = allStages.indexOf(currentStage)
    val totalStages = allStages.size

    // Calculate progress to next stage (if not at max stage)
    val progressToNextStage = if (currentStage == FlowerGrowthStage.BLOOM) {
        1f // Max stage reached
    } else {
        val nextStage = allStages[currentStageIndex + 1]
        val progressWithCurrentStage = currentStreak - currentStage.streakThreshold
        val daysForCurrentStage = nextStage.streakThreshold - currentStage.streakThreshold
        (progressWithCurrentStage.toFloat() / daysForCurrentStage).coerceIn(0f, 1f)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = BloomTheme.colors.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(Res.string.full_growth_path),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // New: Growth System Explanation
            BloomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {}
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BloomTheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(Res.string.growth_system_explanation),
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.primary
                    )
                }
            }

            // Current stage info card
            BloomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            Res.string.stage_progress_info,
                            currentStageIndex + 1,
                            totalStages,
                            currentStage.getTitle()
                        ),
                        style = BloomTheme.typography.subheading,
                        fontWeight = FontWeight.Medium,
                        color = BloomTheme.colors.textColor.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display current streak info
                    Text(
                        text = stringResource(Res.string.current_streak_info, currentStreak),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar to next stage
                    if (currentStage != FlowerGrowthStage.BLOOM) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BloomLinearProgressIndicator(
                                percentage = progressToNextStage,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(
                                        Res.string.stage_threshold_started,
                                        currentStage.streakThreshold
                                    ),
                                    style = BloomTheme.typography.small,
                                    color = BloomTheme.colors.textColor.secondary
                                )

                                Text(
                                    text = stringResource(
                                        Res.string.stage_threshold_next,
                                        allStages[currentStageIndex + 1].streakThreshold
                                    ),
                                    style = BloomTheme.typography.small,
                                    color = BloomTheme.colors.textColor.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(
                                    Res.string.days_to_next_stage,
                                    streaksToNextStage
                                ),
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.accent,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(
                                Res.string.bloom_stage_continued,
                                currentStage.streakThreshold
                            ),
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.textColor.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Stage information legend
            BloomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.growth_stages_explained),
                        style = BloomTheme.typography.subheading,
                        fontWeight = FontWeight.Medium,
                        color = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    StageExplanationItem(
                        stage = FlowerGrowthStage.SEED,
                        descriptionResId = Res.string.stage_description_seed
                    )

                    StageExplanationItem(
                        stage = FlowerGrowthStage.SPROUT,
                        descriptionResId = Res.string.stage_description_sprout
                    )

                    StageExplanationItem(
                        stage = FlowerGrowthStage.BUSH,
                        descriptionResId = Res.string.stage_description_bush
                    )

                    StageExplanationItem(
                        stage = FlowerGrowthStage.BUD,
                        descriptionResId = Res.string.stage_description_bud
                    )

                    StageExplanationItem(
                        stage = FlowerGrowthStage.BLOOM,
                        descriptionResId = Res.string.stage_description_bloom
                    )
                }
            }

            // Growth stages visualization
            Spacer(modifier = Modifier.height(8.dp))

            GrowthStagesPath(
                allStages = allStages,
                currentStage = currentStage,
                flowerType = flowerType,
                modifier = Modifier.fillMaxWidth()
            )

            // Health system explanation
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(Res.string.flower_health_system),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.health_description),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Health levels explanation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Health legend boxes
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BloomTheme.colors.success, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = stringResource(Res.string.health_status_healthy),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BloomTheme.colors.secondary, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = stringResource(Res.string.health_status_wilting),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BloomTheme.colors.error, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = stringResource(Res.string.health_status_critical),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Health system rules
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.health_rule_missing_days),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(Res.string.health_rule_consecutive_misses),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(Res.string.health_rule_recovery),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom padding
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun StageExplanationItem(
    stage: FlowerGrowthStage,
    descriptionResId: StringResource
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when (stage) {
                        FlowerGrowthStage.SEED -> BloomTheme.colors.primary.copy(alpha = 0.7f)
                        FlowerGrowthStage.SPROUT -> BloomTheme.colors.primary.copy(alpha = 0.8f)
                        FlowerGrowthStage.BUSH -> BloomTheme.colors.primary.copy(alpha = 0.9f)
                        FlowerGrowthStage.BUD -> BloomTheme.colors.primary.copy(alpha = 0.95f)
                        FlowerGrowthStage.BLOOM -> BloomTheme.colors.primary
                    }
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${stage.getTitle()}: ",
            style = BloomTheme.typography.small,
            fontWeight = FontWeight.Medium,
            color = BloomTheme.colors.textColor.primary
        )

        Text(
            text = stringResource(descriptionResId),
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

/**
 * A visually appealing representation of the growth stages with connecting lines.
 */
@Composable
private fun GrowthStagesPath(
    allStages: List<FlowerGrowthStage>,
    currentStage: FlowerGrowthStage,
    flowerType: FlowerType,
    modifier: Modifier = Modifier
) {
    val currentStageIndex = allStages.indexOf(currentStage)
    val primaryColor = BloomTheme.colors.primary
    val surfaceColor = BloomTheme.colors.surface

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        allStages.forEachIndexed { index, stage ->
            val isCurrentStage = stage == currentStage
            val isCompletedStage = index <= currentStageIndex
            val icon = flowerType.getFlowerResource(stage)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Draw the connecting line canvas underneath
                if (index < allStages.size - 1) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.Center)
                    ) {
                        val startY = size.height / 2
                        val gradient = if (index < currentStageIndex) {
                            // Completed section
                            Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor
                                )
                            )
                        } else if (index == currentStageIndex) {
                            // Current stage to next stage (gradient from primary to light)
                            Brush.horizontalGradient(
                                colors = listOf(
                                    primaryColor,
                                    surfaceColor
                                )
                            )
                        } else {
                            // Future stages (light color)
                            Brush.horizontalGradient(
                                colors = listOf(
                                    surfaceColor,
                                    surfaceColor
                                )
                            )
                        }

                        drawLine(
                            brush = gradient,
                            start = Offset(0f, startY),
                            end = Offset(size.width, startY),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Stage column with image and text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    // Circle indicator with image
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(if (isCurrentStage) 64.dp else 48.dp)
                            .background(
                                color = when {
                                    isCurrentStage -> BloomTheme.colors.primary.copy(alpha = 0.1f)
                                    isCompletedStage -> BloomTheme.colors.success.copy(alpha = 0.1f)
                                    else -> BloomTheme.colors.background
                                },
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(icon),
                            contentDescription = stage.getTitle(),
                            modifier = Modifier.size(if (isCurrentStage) 48.dp else 36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stage name
                    Text(
                        text = stage.getTitle(),
                        style = if (isCurrentStage) BloomTheme.typography.body else BloomTheme.typography.small,
                        color = when {
                            isCurrentStage -> BloomTheme.colors.primary
                            isCompletedStage -> BloomTheme.colors.success
                            else -> BloomTheme.colors.textColor.secondary
                        },
                        fontWeight = if (isCurrentStage) FontWeight.Medium else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(72.dp)
                    )

                    // Streak threshold
                    Text(
                        text = "${stage.streakThreshold}+",
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}