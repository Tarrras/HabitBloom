package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatTime
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_alarm_clock
import habitbloom.composeapp.generated.resources.reminder_time_label
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * A time picker component that shows a time field and opens a dialog for selection
 *
 * @param modifier The modifier to be applied to the component
 * @param time The currently selected time
 * @param onTimeSelected Callback when time is changed
 * @param use24HourFormat Whether to use 24-hour format (true) or 12-hour format with AM/PM (false)
 */
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    use24HourFormat: Boolean = true
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart)
    ) {
        // Display the selected time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BloomTheme.colors.cardSecondary)
                .clickable { showTimePicker = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_alarm_clock),
                contentDescription = "Time",
                tint = BloomTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(Res.string.reminder_time_label).dropLast(1),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.primary,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formatTime(time, use24HourFormat),
                style = BloomTheme.typography.titleMedium,
                color = BloomTheme.colors.primary,
            )
        }

        // Time picker dialog
        WheelTimePickerDialog(
            isVisible = showTimePicker,
            onDismiss = { showTimePicker = false },
            time = time,
            onTimeSelected = onTimeSelected,
            use24HourFormat = use24HourFormat
        )
    }
}

@Composable
fun NumberPickerColumn(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    formatValue: (Int) -> String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Increase",
            tint = BloomTheme.colors.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    val newValue = if (value + 1 > range.last) range.first else value + 1
                    onValueChange(newValue)
                }
                .padding(4.dp)
        )

        // Current value
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(48.dp)
                .height(48.dp)
                .background(
                    color = BloomTheme.colors.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = BloomTheme.colors.primary,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatValue(value),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center
            )
        }

        // Down arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Decrease",
            tint = BloomTheme.colors.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    val newValue = if (value - 1 < range.first) range.last else value - 1
                    onValueChange(newValue)
                }
                .padding(4.dp)
        )
    }
} 