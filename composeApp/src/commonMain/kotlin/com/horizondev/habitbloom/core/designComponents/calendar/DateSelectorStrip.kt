package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * A horizontal date selector strip that shows a range of dates and allows selecting one.
 *
 * @param modifier The modifier to be applied to the component
 * @param selectedDate The currently selected date
 * @param onDateSelected Callback when a date is selected
 * @param daysToShow Number of days to show in the strip (default 7)
 * @param startFromToday Whether the strip should start from today (default false)
 */
@Composable
fun DateSelectorStrip(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    daysToShow: Int = 7,
    startFromToday: Boolean = false
) {
    val today = getCurrentDate()

    // Calculate the range of dates to display
    val dates = remember(today, daysToShow, startFromToday) {
        val result = mutableListOf<LocalDate>()
        val startDate = if (startFromToday) today else today.calculateStartOfWeek()

        for (i in 0 until daysToShow) {
            result.add(startDate.plus(i, DateTimeUnit.DAY))
        }

        result
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { date ->
            val isSelected = date == selectedDate

            DateItem(
                date = date,
                isSelected = isSelected,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun RowScope.DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = getCurrentDate()
    val isToday = date == today

    val backgroundColor = when {
        isSelected -> BloomTheme.colors.primary
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        else -> BloomTheme.colors.textColor.primary
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = BloomTheme.colors.primary,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onDateSelected(date) }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day number (e.g., "3")
            Text(
                text = date.dayOfMonth.toString(),
                style = BloomTheme.typography.heading,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Day name (e.g., "SAT")
            Text(
                text = date.dayOfWeek.getShortTitle().take(3),
                style = BloomTheme.typography.small,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
} 