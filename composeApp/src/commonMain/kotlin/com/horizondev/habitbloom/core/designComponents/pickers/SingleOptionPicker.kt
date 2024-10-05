package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun <T> SingleOptionPicker(
    modifier: Modifier = Modifier,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    shapeSize: Dp = 36.dp,
    height: Dp = 36.dp,
    backgroundColor: Color = BloomTheme.colors.background,
    content: @Composable BoxScope.(T) -> Unit
) {
    PickerRow(
        modifier = modifier,
        shapeSize = shapeSize,
        backgroundColor = backgroundColor,
        height = height
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
                content(option)
            }
        }
    }
}