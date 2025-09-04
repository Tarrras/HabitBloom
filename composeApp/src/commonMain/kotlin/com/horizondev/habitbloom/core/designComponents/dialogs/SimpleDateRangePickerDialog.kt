package com.horizondev.habitbloom.core.designComponents.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.DateRangeSelectionCalendar
import com.horizondev.habitbloom.core.designComponents.calendar.DateSelection
import com.horizondev.habitbloom.core.designComponents.components.BloomHorizontalDivider
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.close
import habitbloom.composeapp.generated.resources.days_count
import habitbloom.composeapp.generated.resources.one_month
import habitbloom.composeapp.generated.resources.one_week
import habitbloom.composeapp.generated.resources.quick_selection
import habitbloom.composeapp.generated.resources.save_selection
import habitbloom.composeapp.generated.resources.select_date_range
import habitbloom.composeapp.generated.resources.three_months
import habitbloom.composeapp.generated.resources.two_weeks
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitDateRangePickerDialog(
    startDate: LocalDate?,
    endDate: LocalDate? = null,
    maxDurationDays: Int = 90,
    onDatesSelected: (start: LocalDate, end: LocalDate) -> Unit,
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

    // Calculate number of days in the selection (inclusive)
    val selectedDaysCount = when {
        selection.startDate != null && selection.endDate != null ->
            (selection.startDate?.daysUntil(selection.endDate!!) ?: 0) + 1

        selection.startDate != null -> 1
        else -> 0
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BloomTheme.colors.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with title (left), close button (right), and subtitle range
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.select_date_range),
                        style = BloomTheme.typography.headlineMedium,
                        color = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        color = BloomTheme.colors.surface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onDismiss)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(Res.string.close),
                                tint = BloomTheme.colors.textColor.primary
                            )
                        }
                    }
                }

                // Subtitle with short date range and days count in parentheses
                if (selection.startDate != null && selection.endDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val start = selection.startDate!!
                    val end = selection.endDate!!
                    val startShort = "${start.day} ${start.month.getShortTitle().lowercase()}."
                    val endShort = "${end.day} ${end.month.getShortTitle().lowercase()}."
                    val daysText = pluralStringResource(
                        Res.plurals.days_count,
                        selectedDaysCount,
                        selectedDaysCount
                    )
                    Text(
                        text = "$startShort - $endShort ($daysText)",
                        style = BloomTheme.typography.bodyMedium,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            // Quick actions
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(Res.string.quick_selection),
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.textColor.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        8.dp
                    ),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val presets = listOf(
                        7 to stringResource(Res.string.one_week),
                        14 to stringResource(Res.string.two_weeks),
                        30 to stringResource(Res.string.one_month),
                        90 to stringResource(Res.string.three_months)
                    )
                    presets.forEach { (days, label) ->
                        Surface(
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                BloomTheme.colors.glassBorder
                            ),
                            modifier = Modifier.clickable(enabled = selection.startDate != null) {
                                val start = selection.startDate ?: return@clickable
                                val limited = (days).coerceAtMost(maxDurationDays)
                                val end = start.plus(limited - 1, DateTimeUnit.DAY)
                                selection = selection.copy(endDate = end)
                            }
                        ) {
                            Text(
                                text = label,
                                style = BloomTheme.typography.labelMedium,
                                color = BloomTheme.colors.textColor.secondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            DateRangeSelectionCalendar(
                selection = selection,
                onSelectionChanged = { newSelection ->
                    val startDateSel = newSelection.startDate
                    val endDateSel = newSelection.endDate

                    if (startDateSel != null && endDateSel != null) {
                        val days = startDateSel.daysUntil(endDateSel)
                        if (days > maxDurationDays) {
                            val limitedEndDate =
                                startDateSel.plus(maxDurationDays - 1, DateTimeUnit.DAY)
                            selection = newSelection.copy(endDate = limitedEndDate)
                        } else {
                            selection = newSelection
                        }
                    } else {
                        selection = newSelection
                    }
                },
                minDate = minDate,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                maxDurationDays = maxDurationDays
            )


            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BloomSmallActionButton(
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                BloomPrimaryFilledButton(
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp),
                    text = stringResource(Res.string.save_selection),
                    onClick = {
                        if (selection.startDate != null && selection.endDate != null) {
                            onDatesSelected(selection.startDate!!, selection.endDate!!)
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    enabled = selection.startDate != null && selection.endDate != null
                )
            }
        }
    }
}