package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.save
import habitbloom.composeapp.generated.resources.select_reminder_time
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/**
 * A dialog component for time selection that uses BloomAlertDialog
 *
 * @param isVisible Whether the dialog is currently visible
 * @param onDismiss Callback when the dialog is dismissed
 * @param time The currently selected time
 * @param onTimeSelected Callback when time is confirmed
 * @param use24HourFormat Whether to use 24-hour format (true) or 12-hour format with AM/PM (false)
 */
@Composable
fun TimePickerDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    use24HourFormat: Boolean = true
) {
    // Local copy of time to work with until confirmed
    var currentHour by remember(time) { mutableStateOf(time.hour) }
    var currentMinute by remember(time) { mutableStateOf(time.minute) }

    BloomAlertDialog(
        isShown = isVisible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.select_reminder_time),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hours picker
                Spacer(modifier = Modifier.weight(1f))

                NumberPickerColumn(
                    value = currentHour,
                    onValueChange = { newHour ->
                        currentHour = newHour
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
                    value = currentMinute,
                    onValueChange = { newMinute ->
                        currentMinute = newMinute
                    },
                    range = 0..59,
                    formatValue = { it.toString().padStart(2, '0') }
                )

                // AM/PM picker if using 12-hour format
                if (!use24HourFormat) {
                    Spacer(modifier = Modifier.width(16.dp))

                    val isPM = currentHour >= 12
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
                                        currentHour = currentHour - 12
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
                                        currentHour = currentHour + 12
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

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BloomPrimaryOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss
                )

                Spacer(modifier = Modifier.width(16.dp))

                BloomPrimaryFilledButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.save),
                    onClick = {
                        onTimeSelected(LocalTime(currentHour, currentMinute))
                        onDismiss()
                    }
                )
            }
        }
    }
} 