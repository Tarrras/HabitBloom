package com.horizondev.habitbloom.core.designComponents.switcher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getTitle

@Composable
fun TimeOfDaySwitcher(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    val selectedTabIndex = TimeOfDay.entries.indexOf(selectedTimeOfDay)
    TabRow(
        modifier = modifier.clip(
            shape = RoundedCornerShape(12.dp)
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
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        },
        containerColor = BloomTheme.colors.primary.copy(alpha = 0.2f)
    ) {
        TimeOfDay.entries.forEach {
            val isSelected = it == selectedTimeOfDay
            Text(
                text = it.getTitle(),
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                color = when (isSelected) {
                    true -> BloomTheme.colors.textColor.primary
                    false -> BloomTheme.colors.textColor.secondary
                },
                modifier = Modifier.zIndex(2f).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onTimeOfDaySelected(it)
                }.padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

