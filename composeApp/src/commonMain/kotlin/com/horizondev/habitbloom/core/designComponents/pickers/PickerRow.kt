package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun PickerRow(
    modifier: Modifier = Modifier,
    shapeSize: Dp = 36.dp,
    backgroundColor: Color = BloomTheme.colors.background,
    height: Dp = 40.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .height(height)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(shapeSize)
            ).border(
                width = 2.dp,
                color = BloomTheme.colors.primary,
                shape = RoundedCornerShape(shapeSize)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}