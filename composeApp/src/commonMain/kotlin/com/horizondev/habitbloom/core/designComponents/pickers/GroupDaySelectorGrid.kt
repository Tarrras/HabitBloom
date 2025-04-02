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
import kotlinx.datetime.DayOfWeek

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