package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getShortTitle
import kotlinx.datetime.DayOfWeek

@Composable
fun DayPicker(
    modifier: Modifier = Modifier,
    activeDays: List<DayOfWeek>,
    dayStateChanged: (DayOfWeek, Boolean) -> Unit,
    shapeSize: Dp = 36.dp,
    backgroundColor: Color = BloomTheme.colors.background,
    listOfDays: List<DayOfWeek> = DayOfWeek.entries,
    enabled: Boolean = true
) {
    PickerRow(
        modifier = modifier,
        backgroundColor = backgroundColor,
        shapeSize = shapeSize
    ) {
        listOfDays.forEachIndexed { index, dayOfWeek ->
            val isActiveDay = dayOfWeek in activeDays
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = shapeSize, bottomStart = shapeSize)
                listOfDays.lastIndex -> RoundedCornerShape(
                    topEnd = shapeSize,
                    bottomEnd = shapeSize
                )

                else -> RectangleShape
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isActiveDay) BloomTheme.colors.primary else Color.Transparent,
                        shape = shape
                    )
                    .fillMaxHeight()
                    .clip(shape)
                    .clickable(enabled = enabled) {
                        dayStateChanged(dayOfWeek, !isActiveDay)
                    }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getShortTitle(),
                    color = if (isActiveDay) BloomTheme.colors.textColor.white
                    else BloomTheme.colors.textColor.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}