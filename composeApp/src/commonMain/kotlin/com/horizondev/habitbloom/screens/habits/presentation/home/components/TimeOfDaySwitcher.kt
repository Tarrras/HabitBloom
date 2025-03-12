package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getTitle

@Composable
fun TimeOfDaySwitcher(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeChanged: (TimeOfDay) -> Unit
) {
    val selectedTabIndex = TimeOfDay.entries.indexOf(selectedTimeOfDay)
    TabRow(
        modifier = modifier,
        divider = {},
        selectedTabIndex = selectedTabIndex,
        indicator = @Composable { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 3.dp,
                    color = BloomTheme.colors.primary
                )
            }
        },
        containerColor = Color.Transparent
    ) {
        TimeOfDay.entries.forEach {
            val isSelected = it == selectedTimeOfDay
            Text(
                text = it.getTitle(),
                style = BloomTheme.typography.button,
                color = when (isSelected) {
                    true -> BloomTheme.colors.textColor.primary
                    false -> BloomTheme.colors.textColor.secondary
                },
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onTimeChanged(it)
                }.padding(bottom = 6.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

