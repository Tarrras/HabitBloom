package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun CalendarControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    BloomSurface(
        modifier = modifier.size(36.dp),
        color = BloomTheme.colors.cardSecondary.copy(alpha = 0.75f),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick.takeIf { enabled }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun CalendarLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = BloomTheme.typography.bodySmall,
            color = BloomTheme.colors.textColor.secondary
        )
    }
}

@Composable
fun CalendarStatusDot(
    color: Color?,
    modifier: Modifier = Modifier
) {
    if (color != null) {
        Box(
            modifier = modifier
                .size(7.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
    }
}
