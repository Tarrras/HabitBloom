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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.DateRangeSelectionCalendar
import com.horizondev.habitbloom.core.designComponents.calendar.DateSelection
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.confirm
import habitbloom.composeapp.generated.resources.select_dates
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * A simplified dialog for selecting a date range.
 *
 * @param startDate The currently selected start date
 * @param endDate The currently selected end date (optional)
 * @param onDatesSelected Callback with the selected start date and optional end date
 * @param onDismiss Callback when the dialog is dismissed
 * @param minDate The minimum selectable date (defaults to current date)
 */
@Composable
fun SimpleDateRangePickerDialog(
    startDate: LocalDate?,
    endDate: LocalDate? = null,
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

    // Create a formatted date range string
    val formattedDateRange = when {
        selection.startDate != null && selection.endDate != null -> {
            val startStr = formatDate(selection.startDate!!)
            val endStr = formatDate(selection.endDate!!)
            if (startStr == endStr) startStr else "$startStr → $endStr"
        }

        selection.startDate != null -> formatDate(selection.startDate!!)
        else -> ""
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = BloomTheme.colors.background,
            shadowElevation = 4.dp
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

                // Date range display
                if (formattedDateRange.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = formattedDateRange,
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Calendar component
                DateRangeSelectionCalendar(
                    selection = selection,
                    onSelectionChanged = { selection = it },
                    minDate = minDate,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
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
                            selection.startDate?.let { start ->
                                onDatesSelected(start, selection.endDate)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selection.startDate != null
                    )
                }
            }
        }
    }
}

// Helper function to format dates consistently
private fun formatDate(date: LocalDate): String {
    return "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
} 