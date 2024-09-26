package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getShortTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.next_week
import habitbloom.composeapp.generated.resources.this_week
import kotlinx.datetime.DayOfWeek
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
    PickerRow(
        modifier = modifier,
        shapeSize = shapeSize,
        backgroundColor = backgroundColor,
        height = 32.dp
    ) {
        options.forEachIndexed { index, option ->
            val isActiveOption = selectedOption == option
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = shapeSize, bottomStart = shapeSize)
                options.lastIndex -> RoundedCornerShape(
                    topEnd = shapeSize,
                    bottomEnd = shapeSize
                )

                else -> RectangleShape
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isActiveOption) BloomTheme.colors.primary else Color.Transparent,
                        shape = shape
                    )
                    .fillMaxHeight()
                    .clip(shape)
                    .clickable {
                        onOptionSelected(option)
                    }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = option.getTitle(),
                    color = if (isActiveOption) BloomTheme.colors.textColor.white
                    else BloomTheme.colors.textColor.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

enum class HabitWeekStartOption {
    THIS_WEEK,
    NEXT_WEEK
}

@Composable
fun HabitWeekStartOption.getTitle(): String {
    return when(this) {
        HabitWeekStartOption.THIS_WEEK -> stringResource(Res.string.this_week)
        HabitWeekStartOption.NEXT_WEEK -> stringResource(Res.string.next_week)
    }
}