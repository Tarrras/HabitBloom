package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.am_label
import habitbloom.composeapp.generated.resources.date_created_label
import habitbloom.composeapp.generated.resources.description_label
import habitbloom.composeapp.generated.resources.edit_habit
import habitbloom.composeapp.generated.resources.every_day_label
import habitbloom.composeapp.generated.resources.formatted_date
import habitbloom.composeapp.generated.resources.formatted_time
import habitbloom.composeapp.generated.resources.habit_detail_section_title
import habitbloom.composeapp.generated.resources.ic_edit
import habitbloom.composeapp.generated.resources.pm_label
import habitbloom.composeapp.generated.resources.reminder_time_label
import habitbloom.composeapp.generated.resources.repeat_schedule_label
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Component to display the habit's detailed information and edit option.
 *
 * @param description The habit description
 * @param startDate The date when the habit was created
 * @param repeatDays The days of the week when the habit repeats
 * @param reminderTime The time of day when the reminder is set (if any)
 * @param onEditClick Callback when the edit button is clicked
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun HabitDetailSection(
    description: String,
    startDate: LocalDate,
    repeatDays: List<DayOfWeek>,
    reminderTime: LocalTime?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.habit_detail_section_title),
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = stringResource(Res.string.edit_habit),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(Res.string.edit_habit))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description section
            if (description.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.description_label),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Repeat schedule section
            Text(
                text = stringResource(Res.string.repeat_schedule_label),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            val repeatDaysText = if (repeatDays.size == 7) {
                stringResource(Res.string.every_day_label)
            } else {
                repeatDays.joinToString(", ") {
                    it.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }

            Text(
                text = repeatDaysText,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary
            )

            // Reminder time section
            if (reminderTime != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.reminder_time_label),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(reminderTime),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )
            }

            // Start date section
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.date_created_label),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatDate(startDate),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary
            )
        }
    }
}

/**
 * Formats a LocalTime for display.
 *
 * @param time The time to format
 * @return Formatted time string (e.g., "9:00 AM")
 */
@Composable
private fun formatTime(time: LocalTime): String {
    val hour = time.hour
    val minute = time.minute
    val period =
        if (hour < 12) stringResource(Res.string.am_label) else stringResource(Res.string.pm_label)
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return stringResource(
        Res.string.formatted_time,
        displayHour,
        minute.toString().padStart(2, '0'),
        period
    )
}

/**
 * Formats a LocalDate for display.
 *
 * @param date The date to format
 * @return Formatted date string (e.g., "March 15, 2023")
 */
@Composable
private fun formatDate(date: LocalDate): String {
    val month = date.month.getTitle()

    return stringResource(Res.string.formatted_date, month, date.dayOfMonth, date.year)
} 