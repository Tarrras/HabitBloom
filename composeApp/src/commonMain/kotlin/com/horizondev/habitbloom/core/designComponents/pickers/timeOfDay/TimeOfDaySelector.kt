package com.horizondev.habitbloom.core.designComponents.pickers.timeOfDay

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.horizondev.habitbloom.core.designComponents.pickers.SingleOptionPicker
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getTitle

@Composable
fun TimeOfDaySelector(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    SingleOptionPicker(
        modifier = modifier,
        options = TimeOfDay.entries,
        selectedOption = selectedTimeOfDay,
        backgroundColor = Color.Transparent,
        onOptionSelected = {
            onTimeOfDaySelected(it)
        }, content = { option ->
            Text(
                textAlign = TextAlign.Center,
                text = option.getTitle(),
                color = if (option == selectedTimeOfDay) BloomTheme.colors.textColor.white
                else BloomTheme.colors.textColor.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    )
}