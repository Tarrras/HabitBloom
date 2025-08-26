package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getTitle
import kotlinx.datetime.YearMonth

@Composable
fun CalendarTitle(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            modifier = Modifier.size(24.dp).clip(CircleShape).clickable {
                goToPrevious()
            },
            contentDescription = "previous_month",
            tint = BloomTheme.colors.textColor.primary
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "${currentMonth.month.getTitle()} ${currentMonth.year}",
            style = BloomTheme.typography.subheading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            modifier = Modifier.size(24.dp).clip(CircleShape).clickable {
                goToNext()
            },
            contentDescription = "next_month",
            tint = BloomTheme.colors.textColor.primary
        )
    }
}