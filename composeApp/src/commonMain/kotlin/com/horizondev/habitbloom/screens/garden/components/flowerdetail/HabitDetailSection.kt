package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designComponents.pickers.formatTime
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.check_full_habit_info
import habitbloom.composeapp.generated.resources.date_created_label
import habitbloom.composeapp.generated.resources.description_label
import habitbloom.composeapp.generated.resources.formatted_date
import habitbloom.composeapp.generated.resources.habit_detail_section_title
import habitbloom.composeapp.generated.resources.reminder_time_label
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/**
 * Component to display the habit's detailed information and edit option.
 *
 * @param description The habit description
 * @param startDate The date when the habit was created
 * @param reminderTime The time of day when the reminder is set (if any)
 * @param onCheckFullHabitInfoClick Callback when the edit button is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun HabitDetailSection(
    description: String,
    startDate: LocalDate,
    reminderTime: LocalTime?,
    onCheckFullHabitInfoClick: () -> Unit,
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

            // Reminder time section
            if (reminderTime != null) {

                Text(
                    text = stringResource(Res.string.reminder_time_label),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(reminderTime, use24HourFormat = true),
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

            Spacer(modifier = Modifier.height(12.dp))

            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = stringResource(Res.string.check_full_habit_info),
                onClick = onCheckFullHabitInfoClick
            )
        }
    }
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