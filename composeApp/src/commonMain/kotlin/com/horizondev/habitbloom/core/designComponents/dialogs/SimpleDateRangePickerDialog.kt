package com.horizondev.habitbloom.core.designComponents.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.DateRangeSelectionCalendar
import com.horizondev.habitbloom.core.designComponents.calendar.DateSelection
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.days_count
import habitbloom.composeapp.generated.resources.save_selection
import habitbloom.composeapp.generated.resources.select_dates
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A simplified dialog for selecting a date range.
 *
 * @param startDate The currently selected start date
 * @param endDate The currently selected end date (optional)
 * @param maxDurationDays Maximum allowed duration in days between start and end dates
 * @param onDatesSelected Callback with the selected start date and optional end date
 * @param onDismiss Callback when the dialog is dismissed
 * @param minDate The minimum selectable date (defaults to current date)
 */
@Composable
fun AddHabitDateRangePickerDialog(
    startDate: LocalDate?,
    endDate: LocalDate? = null,
    maxDurationDays: Int = 90,
    onDatesSelected: (start: LocalDate, end: LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDate = getCurrentDate()
) {
    var selection by remember {
        mutableStateOf(
            DateSelection(
                startDate = startDate,
                endDate = endDate
            )
        )
    }

    // Calculate number of days in the selection
    val selectedDaysCount = when {
        selection.startDate != null && selection.endDate != null -> {
            calculateDaysBetween(selection.startDate!!, selection.endDate!!)
        }

        selection.startDate != null -> 1
        else -> 0
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // Limit maximum height to 90% of screen
                .clip(RoundedCornerShape(16.dp)),
            color = BloomTheme.colors.background,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with title and buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BloomTheme.colors.background)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.select_dates),
                        style = BloomTheme.typography.heading,
                        color = BloomTheme.colors.textColor.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Display days count badge
                    if (selectedDaysCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = BloomTheme.colors.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = pluralStringResource(
                                    Res.plurals.days_count,
                                    selectedDaysCount,
                                    selectedDaysCount
                                ),
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons at the top
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cancel button
                        BloomSmallActionButton(
                            text = stringResource(Res.string.cancel),
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Save button
                        BloomPrimaryFilledButton(
                            text = stringResource(Res.string.save_selection),
                            onClick = {
                                selection.startDate?.let { start ->
                                    onDatesSelected(start, selection.endDate)
                                }
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selection.startDate != null && selection.endDate != null
                        )
                    }
                }

                // Calendar component (with its own internal scrolling)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DateRangeSelectionCalendar(
                        selection = selection,
                        onSelectionChanged = { newSelection ->
                            // Apply maximum duration limit
                            val startDate = newSelection.startDate
                            val endDate = newSelection.endDate

                            if (startDate != null && endDate != null) {
                                val days = calculateDaysBetween(startDate, endDate)

                                // If exceeded max duration, limit the end date
                                if (days > maxDurationDays) {
                                    val limitedEndDate =
                                        startDate.plus(maxDurationDays - 1, DateTimeUnit.DAY)
                                    selection = newSelection.copy(endDate = limitedEndDate)
                                } else {
                                    selection = newSelection
                                }
                            } else {
                                selection = newSelection
                            }
                        },
                        minDate = minDate,
                        modifier = Modifier.fillMaxWidth(),
                        maxDurationDays = maxDurationDays
                    )
                }
            }
        }
    }
}

// Helper function to format dates consistently
private fun formatDate(date: LocalDate): String {
    return "${date.dayOfMonth}/${date.monthNumber}/${date.year}"
}

/**
 * Calculate days between two dates (inclusive)
 */
private fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Int {
    if (startDate > endDate) return 0

    var current = startDate
    var days = 1 // Start with 1 to include the start date

    while (current < endDate) {
        days++
        current = current.plus(1, DateTimeUnit.DAY)
    }

    return days
} 