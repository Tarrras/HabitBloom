package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.next_week
import habitbloom.composeapp.generated.resources.this_week
import org.jetbrains.compose.resources.stringResource

@Composable
fun SingleWeekStartOptionPicker(
    modifier: Modifier = Modifier,
    selectedOption: HabitWeekStartOption,
    options: List<HabitWeekStartOption>,
    onOptionSelected: (HabitWeekStartOption) -> Unit,
    shapeSize: Dp = 36.dp,
    backgroundColor: Color = BloomTheme.colors.background,
) {
    SingleOptionPicker(
        modifier = modifier,
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        shapeSize = shapeSize,
        backgroundColor = backgroundColor
    ) { option ->
        Text(
            textAlign = TextAlign.Center,
            text = option.getTitle(),
            color = if (option == selectedOption) BloomTheme.colors.textColor.white
            else BloomTheme.colors.textColor.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

enum class HabitWeekStartOption {
    THIS_WEEK,
    NEXT_WEEK
}

@Composable
fun HabitWeekStartOption.getTitle(): String {
    return when (this) {
        HabitWeekStartOption.THIS_WEEK -> stringResource(Res.string.this_week)
        HabitWeekStartOption.NEXT_WEEK -> stringResource(Res.string.next_week)
    }
}