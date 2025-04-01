package com.horizondev.habitbloom.core.designComponents.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.pickers.DateRangePicker
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.confirm
import habitbloom.composeapp.generated.resources.select_dates
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

/**
 * A dialog for selecting a date range.
 *
 * @param startDate The currently selected start date
 * @param endDate The currently selected end date
 * @param onDatesSelected Callback with the selected start and end dates
 * @param onDismiss Callback when the dialog is dismissed
 * @param minDate The minimum selectable date
 * @param maxDate The maximum selectable date
 */
@Composable
fun DateRangePickerDialog(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDatesSelected: (start: LocalDate, end: LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDate? = getCurrentDate(),
    maxDate: LocalDate? = null
) {
    var tempStartDate by remember(startDate) { mutableStateOf(startDate) }
    var tempEndDate by remember(endDate) { mutableStateOf(endDate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = BloomTheme.colors.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dialog title
                Text(
                    text = stringResource(Res.string.select_dates),
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date picker
                DateRangePicker(
                    startDate = tempStartDate ?: getCurrentDate(),
                    endDate = tempEndDate,
                    onStartDateSelected = { tempStartDate = it },
                    onEndDateSelected = { tempEndDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    minDate = minDate ?: getCurrentDate(),
                    maxDate = maxDate ?: getCurrentDate().plus(1, DateTimeUnit.YEAR)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BloomSmallActionButton(
                        text = stringResource(Res.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    BloomSmallActionButton(
                        text = stringResource(Res.string.confirm),
                        onClick = {
                            tempStartDate?.let { start ->
                                onDatesSelected(start, tempEndDate)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = tempStartDate != null
                    )
                }
            }
        }
    }
} 