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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import habitbloom.composeapp.generated.resources.congratulations_full_bloom
import habitbloom.composeapp.generated.resources.days_to_next_stage
import habitbloom.composeapp.generated.resources.full_growth_path
import habitbloom.composeapp.generated.resources.stage_progress_info
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(Res.string.full_growth_path),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Current stage info card
            BloomCard(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 24.dp),
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

                    // Progress bar to next stage
                    if (currentStage != FlowerGrowthStage.BLOOM) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BloomLinearProgressIndicator(
                                percentage = progressToNextStage,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(
                                    Res.string.days_to_next_stage,
                                    streaksToNextStage
                                ),
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.secondary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(Res.string.congratulations_full_bloom),
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.success,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Growth stages visualization
            Spacer(modifier = Modifier.height(16.dp))

            GrowthStagesPath(
                allStages = allStages,
                currentStage = currentStage,
                flowerType = flowerType,
                modifier = Modifier.fillMaxWidth()
            )

            // Bottom padding
            Spacer(modifier = Modifier.height(48.dp))
        }
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