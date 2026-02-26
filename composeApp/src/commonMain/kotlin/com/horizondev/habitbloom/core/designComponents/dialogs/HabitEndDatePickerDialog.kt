package com.horizondev.habitbloom.core.designComponents.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.DateRangeSelectionCalendar
import com.horizondev.habitbloom.core.designComponents.calendar.DateSelection
import com.horizondev.habitbloom.core.designComponents.components.BloomHorizontalDivider
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatDate
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.habit_schedule_end_date_picker_title
import habitbloom.composeapp.generated.resources.habit_schedule_fixed_start_title
import habitbloom.composeapp.generated.resources.save
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEndDatePickerDialog(
    startDate: LocalDate?,
    endDate: LocalDate?,
    maxDurationDays: Int = 90,
    onEndDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    if (startDate == null || endDate == null) return

    val today = getCurrentDate()
    val minEndDate = if (startDate > today) startDate else today
    val maxEndDate = startDate.plus(maxDurationDays - 1, DateTimeUnit.DAY)
    val initialEndDate = clampDate(endDate, minEndDate, maxEndDate)

    var selection by remember(startDate, endDate) {
        mutableStateOf(DateSelection(startDate = startDate, endDate = initialEndDate))
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BloomTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(Res.string.habit_schedule_end_date_picker_title),
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.habit_schedule_fixed_start_title, formatDate(startDate)),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            DateRangeSelectionCalendar(
                modifier = Modifier.fillMaxWidth(),
                selection = selection,
                onSelectionChanged = { updatedSelection ->
                    val pickedDate = when {
                        updatedSelection.endDate != null -> updatedSelection.endDate
                        updatedSelection.startDate != null && updatedSelection.startDate != startDate -> {
                            updatedSelection.startDate
                        }

                        else -> null
                    }

                    if (pickedDate != null) {
                        selection = DateSelection(
                            startDate = startDate,
                            endDate = clampDate(pickedDate, minEndDate, maxEndDate)
                        )
                    }
                },
                minDate = minEndDate,
                maxDurationDays = maxDurationDays
            )

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloomSmallActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss
                )

                BloomPrimaryFilledButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.save),
                    onClick = {
                        selection.endDate?.let(onEndDateSelected)
                        onDismiss()
                    },
                    enabled = selection.endDate != null
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun clampDate(
    date: LocalDate,
    min: LocalDate,
    max: LocalDate
): LocalDate {
    return when {
        date < min -> min
        date > max -> max
        else -> date
    }
}
