package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.clippedShadow

@Composable
fun UserHabitCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitRecordFullInfo,
    editModeEnabled: Boolean,
    onCompletionStatusChanged: (Long, Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    val completed = habitInfo.isCompleted
    val wholeItemMutableInteractionState = remember { MutableInteractionSource() }
    val completionMutableInteractionState = remember { MutableInteractionSource() }

    val isWholeItemPressed by wholeItemMutableInteractionState.collectIsPressedAsState()
    val isCompletionPressed by completionMutableInteractionState.collectIsPressedAsState()
    val isPressed = isWholeItemPressed || isCompletionPressed

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            else -> 1f
        },
        animationSpec = tween(200)
    )

    val bgBrush = if (completed) {
        Brush.horizontalGradient(
            colors = listOf(
                BloomTheme.colors.primary.copy(alpha = 0.1f),
                BloomTheme.colors.secondary.copy(alpha = 0.1f)
            )
        )
    } else Brush.horizontalGradient(
        colors = listOf(
            BloomTheme.colors.card.copy(alpha = 0.4f),
            BloomTheme.colors.card.copy(alpha = 0.4f),
        )
    )

    val borderColor = if (completed) BloomTheme.colors.primary.copy(alpha = 0.3f)
    else BloomTheme.colors.border.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clippedShadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(bgBrush)
            .clickable(
                indication = ripple(),
                interactionSource = wholeItemMutableInteractionState,
                onClick = { onClick() }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                BloomNetworkImage(
                    iconUrl = habitInfo.iconUrl,
                    contentDescription = habitInfo.name,
                    size = 56.dp,
                    shape = CircleShape
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habitInfo.name,
                    color = if (completed) BloomTheme.colors.foreground.copy(alpha = 0.75f)
                    else BloomTheme.colors.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (completed) BloomTheme.colors.primary else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (completed) BloomTheme.colors.primary else BloomTheme.colors.mutedForeground.copy(
                            alpha = 0.3f
                        ),
                        shape = RoundedCornerShape(50)
                    ).clickable(
                        enabled = editModeEnabled,
                        interactionSource = completionMutableInteractionState,
                        indication = null,
                        onClick = {
                            onCompletionStatusChanged(habitInfo.id, habitInfo.isCompleted.not())
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                this@Row.AnimatedVisibility(
                    visible = completed,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                    )
                }
            }
        }
    }
}