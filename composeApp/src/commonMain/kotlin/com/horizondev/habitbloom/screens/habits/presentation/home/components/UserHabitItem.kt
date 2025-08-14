package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.checkbox.BloomCheckBox
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
import com.horizondev.habitbloom.utils.formatDueInMinutes
import com.horizondev.habitbloom.utils.getChartBorder
import com.horizondev.habitbloom.utils.getChartColor
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.level_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserHabitItem(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitRecordFullInfo,
    editModeEnabled: Boolean,
    vitalityPercent: Int? = null,
    dueInMinutes: Int? = null,
    onCompletionStatusChanged: (Long, Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    val isCompleted = habitInfo.isCompleted

    BloomCard(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = habitInfo.timeOfDay.getChartColor()
        ),
        border = BorderStroke(width = 3.dp, color = habitInfo.timeOfDay.getChartBorder()),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with vitality ring
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(BloomTheme.colors.surface, CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    BloomNetworkImage(
                        iconUrl = habitInfo.iconUrl,
                        size = 44.dp,
                        contentDescription = habitInfo.name,
                    )
                    // Vitality arc overlay (simple colored halo)
                    if (vitalityPercent != null) {
                        val color = when {
                            vitalityPercent >= 65 -> BloomTheme.colors.success
                            vitalityPercent >= 20 -> BloomTheme.colors.secondary
                            else -> BloomTheme.colors.error
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(color.copy(alpha = 0.10f), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habitInfo.name,
                            style = BloomTheme.typography.heading,
                            color = BloomTheme.colors.textColor.primary,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Level chip placeholder (uses level string for now)
                        Text(
                            text = stringResource(Res.string.level_label, 1),
                            style = BloomTheme.typography.small,
                            color = BloomTheme.colors.primary,
                            modifier = Modifier
                                .background(
                                    color = BloomTheme.colors.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habitInfo.description,
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        // Due soon hint
                        if (dueInMinutes != null) {
                            val dueText = formatDueInMinutes(dueInMinutes)
                            Text(
                                text = dueText,
                                style = BloomTheme.typography.small,
                                color = BloomTheme.colors.textColor.secondary,
                                modifier = Modifier
                                    .background(
                                        color = BloomTheme.colors.primary.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Completion control: only a checkbox, enabled only for today
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (editModeEnabled) {
                            BloomCheckBox(
                                size = 28.dp,
                                checked = habitInfo.isCompleted,
                                onCheckedChange = { isChecked ->
                                    onCompletionStatusChanged(habitInfo.id, isChecked)
                                },
                                iconSize = 16.dp,
                                shape = RoundedCornerShape(8.dp),
                            )
                        }
                    }
                }
            }

            // Remove streak badge; XP/level UI lives in detail for now
        }
    }
}