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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import kotlinx.datetime.LocalTime

/**
 * A time picker component that allows selecting hours and minutes
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
                .border(1.dp, BloomTheme.colors.secondary, RoundedCornerShape(8.dp))
                .background(BloomTheme.colors.surface)
                .clickable { showTimePicker = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Time",
                tint = BloomTheme.colors.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formatTime(time, use24HourFormat),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.weight(1f)
            )
        }

        // Time picker dropdown
        DropdownMenu(
            expanded = showTimePicker,
            onDismissRequest = { showTimePicker = false },
            modifier = Modifier
                .background(BloomTheme.colors.background)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        style = BloomTheme.typography.subheading,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hours picker
                        NumberPickerColumn(
                            value = time.hour,
                            onValueChange = { newHour ->
                                onTimeSelected(LocalTime(newHour, time.minute))
                            },
                            range = if (use24HourFormat) 0..23 else 1..12,
                            formatValue = { it.toString().padStart(2, '0') }
                        )

                        Text(
                            text = ":",
                            style = BloomTheme.typography.heading,
                            color = BloomTheme.colors.textColor.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Minutes picker
                        NumberPickerColumn(
                            value = time.minute,
                            onValueChange = { newMinute ->
                                onTimeSelected(LocalTime(time.hour, newMinute))
                            },
                            range = 0..59,
                            formatValue = { it.toString().padStart(2, '0') }
                        )

                        // AM/PM picker if using 12-hour format
                        if (!use24HourFormat) {
                            Spacer(modifier = Modifier.width(16.dp))

                            val isPM = time.hour >= 12
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (!isPM) BloomTheme.colors.primary
                                            else BloomTheme.colors.surface
                                        )
                                        .clickable {
                                            if (isPM) {
                                                // Convert from PM to AM
                                                val newHour = time.hour - 12
                                                onTimeSelected(
                                                    LocalTime(
                                                        if (newHour == 0) 0 else newHour,
                                                        time.minute
                                                    )
                                                )
                                            }
                                        }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AM",
                                        style = BloomTheme.typography.body,
                                        color = if (!isPM) BloomTheme.colors.textColor.white
                                        else BloomTheme.colors.textColor.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isPM) BloomTheme.colors.primary
                                            else BloomTheme.colors.surface
                                        )
                                        .clickable {
                                            if (!isPM) {
                                                // Convert from AM to PM
                                                val newHour = time.hour + 12
                                                onTimeSelected(
                                                    LocalTime(
                                                        if (newHour == 24) 12 else newHour,
                                                        time.minute
                                                    )
                                                )
                                            }
                                        }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "PM",
                                        style = BloomTheme.typography.body,
                                        color = if (isPM) BloomTheme.colors.textColor.white
                                        else BloomTheme.colors.textColor.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Done button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BloomTheme.colors.primary)
                            .clickable { showTimePicker = false }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            style = BloomTheme.typography.body,
                            fontWeight = FontWeight.Bold,
                            color = BloomTheme.colors.textColor.white
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPickerColumn(
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
                    color = BloomTheme.colors.secondary,
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

private fun formatTime(time: LocalTime, use24HourFormat: Boolean): String {
    return if (use24HourFormat) {
        "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    } else {
        val hour = when (time.hour) {
            0 -> 12
            in 1..12 -> time.hour
            else -> time.hour - 12
        }
        val period = if (time.hour >= 12) "PM" else "AM"
        "${hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')} $period"
    }
} 