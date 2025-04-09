package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.roundToDecimal
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.flower_health
import habitbloom.composeapp.generated.resources.health_description
import habitbloom.composeapp.generated.resources.health_missed_days
import habitbloom.composeapp.generated.resources.health_rule_consecutive_misses
import habitbloom.composeapp.generated.resources.health_rule_missing_days
import habitbloom.composeapp.generated.resources.health_rule_recovery
import habitbloom.composeapp.generated.resources.health_status_critical
import habitbloom.composeapp.generated.resources.health_status_healthy
import habitbloom.composeapp.generated.resources.health_status_wilting
import habitbloom.composeapp.generated.resources.ic_solid_water_drop
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * A component that displays the flower's health as a visual bar with sections.
 *
 * @param flowerHealth The current health status of the flower
 * @param modifier Optional modifier for customizing the component
 */
@Composable
fun FlowerHealthBar(
    flowerHealth: FlowerHealth,
    modifier: Modifier = Modifier
) {
    // Round health value to avoid floating point precision issues
    val roundedHealthValue = flowerHealth.value.roundToDecimal(1)
    
    val animatedHealthValue by animateFloatAsState(
        targetValue = roundedHealthValue,
        animationSpec = tween(durationMillis = 500)
    )

    // Health bar color based on current health
    val healthColor by animateColorAsState(
        targetValue = when {
            roundedHealthValue >= FlowerHealth.HEALTHY_THRESHOLD -> BloomTheme.colors.success
            roundedHealthValue >= FlowerHealth.WILTING_THRESHOLD -> BloomTheme.colors.secondary
            else -> BloomTheme.colors.error
        },
        animationSpec = tween(durationMillis = 300)
    )

    // Define sections based on health thresholds
    val thresholds = remember {
        listOf(
            0.0f,
            FlowerHealth.WILTING_THRESHOLD,
            FlowerHealth.HEALTHY_THRESHOLD,
            1.0f
        )
    }

    // Health status labels
    val healthStatus = when {
        roundedHealthValue >= FlowerHealth.HEALTHY_THRESHOLD -> stringResource(Res.string.health_status_healthy)
        roundedHealthValue >= FlowerHealth.WILTING_THRESHOLD -> stringResource(Res.string.health_status_wilting)
        else -> stringResource(Res.string.health_status_critical)
    }

    // Display the formatted health value
    val healthValueText = "${(roundedHealthValue * 100).toInt()}%"

    BloomCard(
        modifier = modifier,
        onClick = {}
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header with current health status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.flower_health),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.weight(1f)
                )

                // Missed days indicator
                if (flowerHealth.consecutiveMissedDays > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.health_missed_days) + ": ${flowerHealth.consecutiveMissedDays}",
                            style = BloomTheme.typography.body,
                            color = if (flowerHealth.consecutiveMissedDays >= 3) BloomTheme.colors.error else BloomTheme.colors.textColor.secondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "$healthStatus ($healthValueText)",
                    style = BloomTheme.typography.body,
                    color = healthColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Water droplet icon for health indication
                Icon(
                    painter = painterResource(Res.drawable.ic_solid_water_drop),
                    contentDescription = "Health",
                    tint = healthColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Health bar with threshold markers and current health indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BloomTheme.colors.background)
            ) {
                // Health bar fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedHealthValue)
                        .height(24.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    healthColor.copy(alpha = 0.7f),
                                    healthColor
                                )
                            )
                        )
                )

                // Threshold markers
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    // Draw threshold markers
                    thresholds.forEach { threshold ->
                        if (threshold > 0 && threshold < 1) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.7f),
                                start = Offset(size.width * threshold, 0f),
                                end = Offset(size.width * threshold, size.height),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Consecutive missed days indicator (if any)
                if (flowerHealth.consecutiveMissedDays > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${flowerHealth.consecutiveMissedDays}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
                            )
                        }
                    }
                }
            }

            // Legend for health thresholds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HealthLegendItem(
                    color = BloomTheme.colors.error,
                    label = stringResource(Res.string.health_status_critical),
                    modifier = Modifier.weight(1f)
                )

                HealthLegendItem(
                    color = BloomTheme.colors.secondary,
                    label = stringResource(Res.string.health_status_wilting),
                    modifier = Modifier.weight(1f)
                )

                HealthLegendItem(
                    color = BloomTheme.colors.success,
                    label = stringResource(Res.string.health_status_healthy),
                    modifier = Modifier.weight(1f)
                )
            }

            // Brief description of the health system
            Text(
                text = stringResource(Res.string.health_description),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )

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
    }
}

@Composable
private fun HealthLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = label,
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            fontSize = 10.sp
        )
    }
} 