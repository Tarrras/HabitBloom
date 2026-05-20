package com.horizondev.habitbloom.core.designComponents.switcher

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val periods = remember { TimeOfDay.entries }
    val currentOnTimeOfDaySelected by rememberUpdatedState(onTimeOfDaySelected)
    val selectedTabIndex = selectedTimeOfDay.ordinal
    val outerShape = remember { RoundedCornerShape(24.dp) }
    val tabShape = remember { RoundedCornerShape(16.dp) }
    val primary = BloomTheme.colors.primary
    val primaryVariant = BloomTheme.colors.primaryVariant
    val bgBrush = remember(primary, primaryVariant) {
        Brush.linearGradient(colors = listOf(primary, primaryVariant))
    }

    TabRow(
        modifier = modifier.clip(shape = outerShape),
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
                            shape = tabShape
                        )
                )
            }
        },
        containerColor = BloomTheme.colors.glassBackgroundStrong
    ) {
        periods.forEach { period ->
            TimeOfDayTab(
                period = period,
                isSelected = period == selectedTimeOfDay,
                selectedColor = BloomTheme.colors.primaryForeground,
                unselectedColor = BloomTheme.colors.mutedForeground,
                shape = tabShape,
                onSelected = currentOnTimeOfDaySelected
            )
        }
    }
}

@Composable
private fun TimeOfDayTab(
    period: TimeOfDay,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    shape: RoundedCornerShape,
    onSelected: (TimeOfDay) -> Unit
) {
    val onClick = remember(period, onSelected) {
        { onSelected(period) }
    }
    val textColor = if (isSelected) selectedColor else unselectedColor

    Box(
        modifier = Modifier
            .zIndex(2f)
            .clip(shape)
            .clickable(
                enabled = !isSelected,
                onClick = onClick
            )
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
