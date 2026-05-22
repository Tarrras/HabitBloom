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
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatDate
import com.horizondev.habitbloom.utils.formatDateRange
import com.horizondev.habitbloom.utils.formatTime
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.check_full_habit_info
import habitbloom.composeapp.generated.resources.date_range_label
import habitbloom.composeapp.generated.resources.description_label
import habitbloom.composeapp.generated.resources.habit_detail_section_title
import habitbloom.composeapp.generated.resources.reminder_time_label
import habitbloom.composeapp.generated.resources.start_date_label
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource


@Composable
fun HabitDetailSection(
    description: String,
    startDate: LocalDate,
    endDate: LocalDate? = null,
    reminderTime: LocalTime?,
    use24HourFormat: Boolean,
    onCheckFullHabitInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BloomCard(
        modifier = modifier,
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

            if (reminderTime != null) {

                Text(
                    text = stringResource(Res.string.reminder_time_label),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(reminderTime, use24HourFormat = use24HourFormat),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (endDate != null && startDate != endDate) {
                Text(
                    text = stringResource(Res.string.date_range_label),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatDateRange(startDate, endDate),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary
                )
            } else {
                Text(
                    text = stringResource(Res.string.start_date_label),
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

            Spacer(modifier = Modifier.height(12.dp))

            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = stringResource(Res.string.check_full_habit_info),
                onClick = onCheckFullHabitInfoClick
            )
        }
    }
}
