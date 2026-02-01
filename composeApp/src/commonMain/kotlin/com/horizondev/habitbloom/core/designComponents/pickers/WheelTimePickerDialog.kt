package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.components.BloomHorizontalDivider
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.formatTime
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.close
import habitbloom.composeapp.generated.resources.quick_selection
import habitbloom.composeapp.generated.resources.save
import habitbloom.composeapp.generated.resources.select_reminder_time
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelTimePickerDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    use24HourFormat: Boolean = false
) {
    var internalTime by remember(time) { mutableStateOf(time) }
    if (!isVisible) return

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
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.select_reminder_time),
                    style = BloomTheme.typography.headlineMedium,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(shape = RoundedCornerShape(16.dp))
                        .clickable(onClick = onDismiss)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(Res.string.close),
                        tint = BloomTheme.colors.textColor.primary,
                        modifier = Modifier.align(Alignment.Center).size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTime(internalTime, use24HourFormat = true),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // Quick selection section
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    val quickTimes = listOf(
                        LocalTime(7, 0), LocalTime(8, 0), LocalTime(9, 0), LocalTime(12, 0),
                        LocalTime(15, 0), LocalTime(18, 0), LocalTime(21, 0), LocalTime(22, 0)
                    )
                    quickTimes.forEach { qt ->
                        Surface(
                            color = BloomTheme.colors.surface,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, BloomTheme.colors.glassBorder),
                            modifier = Modifier.clickable {
                                internalTime = qt
                            }
                        ) {
                            Text(
                                text = formatTime(qt, use24HourFormat = true),
                                style = BloomTheme.typography.labelMedium,
                                color = BloomTheme.colors.textColor.secondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            WheelTimePicker(
                time = internalTime,
                onTimeChanged = { internalTime = it },
                use24HourFormat = use24HourFormat
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloomSmallActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss
            )

            Spacer(modifier = Modifier.height(8.dp))

            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.save),
                onClick = {
                    onTimeSelected(internalTime)
                    onDismiss()
                }
            )
        }
    }
}


