package com.horizondev.habitbloom.core.designComponents.switcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getIcon
import com.horizondev.habitbloom.utils.getTitle

@Composable
fun TimeOfDaySwitcher(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    val periods = TimeOfDay.entries

    val selectedTabIndex = TimeOfDay.entries.indexOf(selectedTimeOfDay)
    val bgBrush = Brush.linearGradient(
        colors = listOf(
            BloomTheme.colors.primary,
            BloomTheme.colors.primaryVariant
        )
    )

    TabRow(
        modifier = modifier.clip(
            shape = RoundedCornerShape(24.dp)
        ),
        divider = {},
        selectedTabIndex = selectedTabIndex,
        indicator = @Composable { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .fillMaxHeight()
                        .padding(all = 4.dp)
                        .background(
                            brush = bgBrush,
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }
        },
        containerColor = BloomTheme.colors.glassBackgroundStrong
    ) {
        periods.forEach { period ->
            val isSelected = period == selectedTimeOfDay

            val textColor by animateColorAsState(
                if (isSelected) BloomTheme.colors.primaryForeground
                else BloomTheme.colors.mutedForeground
            )

            Box(
                modifier = Modifier
                    .zIndex(2f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTimeOfDaySelected(period) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = period.getIcon(),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = period.getTitle(),
                        fontSize = 14.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

