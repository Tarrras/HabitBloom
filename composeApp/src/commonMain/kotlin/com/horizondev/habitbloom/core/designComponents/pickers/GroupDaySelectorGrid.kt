package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.GroupOfDays
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.custom_pattern
import habitbloom.composeapp.generated.resources.days_count
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.quick_action
import habitbloom.composeapp.generated.resources.work_days
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

/**
 * A visual grid component for selecting common day patterns.
 * Shows calendar-like visual representations of each pattern.
 *
 * @param selectedGroup Currently selected group (or null if custom selection)
 * @param activeDays Currently selected individual days
 * @param onGroupSelected Callback when a group is selected
 * @param modifier Modifier for styling
 */
@Composable
fun GroupDaySelectorGrid(
    selectedGroup: GroupOfDays?,
    activeDays: List<DayOfWeek>,
    onGroupSelected: (GroupOfDays) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustomSelection = selectedGroup == null && activeDays.isNotEmpty()

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.quick_action),
            color = BloomTheme.colors.textColor.secondary,
            style = BloomTheme.typography.subheading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Every Day option
            PatternOption(
                title = stringResource(Res.string.every_day),
                isSelected = selectedGroup == GroupOfDays.EVERY_DAY,
                onClick = { onGroupSelected(GroupOfDays.EVERY_DAY) },
                highlightedDays = DayOfWeek.entries,
                modifier = Modifier.weight(1f)
            )

            // Work Days option
            PatternOption(
                title = stringResource(Res.string.work_days),
                isSelected = selectedGroup == GroupOfDays.WORK_DAYS,
                onClick = { onGroupSelected(GroupOfDays.WORK_DAYS) },
                highlightedDays = listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
                ),
                modifier = Modifier.weight(1f)
            )

            // Weekends option
            PatternOption(
                title = stringResource(Res.string.only_weekends),
                isSelected = selectedGroup == GroupOfDays.WEEKENDS,
                onClick = { onGroupSelected(GroupOfDays.WEEKENDS) },
                highlightedDays = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                modifier = Modifier.weight(1f)
            )
        }

        if (isCustomSelection) {
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = BloomTheme.colors.surface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = BloomTheme.colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.custom_pattern),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = stringResource(Res.string.days_count, activeDays.size),
                        style = BloomTheme.typography.small,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }
        }
    }
}

/**
 * A single pattern option with a mini calendar visualization.
 */
@Composable
private fun PatternOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    highlightedDays: List<DayOfWeek>,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) BloomTheme.colors.primary.copy(alpha = 0.1f) else BloomTheme.colors.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BloomTheme.colors.primary else BloomTheme.colors.surface
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // Mini week visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayOfWeek.entries.forEach { day ->
                    val isHighlighted = day in highlightedDays
                    DayDot(
                        isHighlighted = isHighlighted,
                        isSelected = isSelected,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * A dot representing a day in the mini week visualization.
 */
@Composable
private fun DayDot(
    isHighlighted: Boolean,
    isSelected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isHighlighted && isSelected -> BloomTheme.colors.primary
                    isHighlighted -> BloomTheme.colors.primary.copy(alpha = 0.6f)
                    else -> BloomTheme.colors.background
                }
            )
    )
} 