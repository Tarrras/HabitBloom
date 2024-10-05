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
import habitbloom.composeapp.generated.resources.month
import habitbloom.composeapp.generated.resources.week
import habitbloom.composeapp.generated.resources.year
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeUnitOptionPicker(
    modifier: Modifier = Modifier,
    selectedOption: TimeUnit,
    options: List<TimeUnit> = TimeUnit.entries,
    onOptionSelected: (TimeUnit) -> Unit,
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

enum class TimeUnit {
    WEEK,
    MONTH,
    YEAR
}

@Composable
fun TimeUnit.getTitle(): String {
    return when (this) {
        TimeUnit.WEEK -> stringResource(resource = Res.string.week)
        TimeUnit.MONTH -> stringResource(resource = Res.string.month)
        TimeUnit.YEAR -> stringResource(resource = Res.string.year)
    }
}