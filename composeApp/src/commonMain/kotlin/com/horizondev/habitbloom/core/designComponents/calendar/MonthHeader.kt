package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getShortTitle
import kotlinx.datetime.DayOfWeek

@Composable
fun MonthHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        daysOfWeek.forEach { day ->
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = day.getShortTitle(),
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                color = BloomTheme.colors.textColor.primary
            )
        }
    }
}