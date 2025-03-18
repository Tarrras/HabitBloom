package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizondev.habitbloom.core.designComponents.switcher.BloomTabSwitcher
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
) {
    BloomTabSwitcher(
        modifier = modifier,
        items = options,
        selectedItem = selectedOption,
        onItemSelected = onOptionSelected,
    ) { option ->
        option.getTitle()
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