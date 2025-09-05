package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

/**
 * A wheel-style time picker with hours, minutes and optional AM/PM column.
 * Matches the iOS-like wheel UX shown in the screenshot, using snapping LazyColumns.
 */
@Composable
fun WheelTimePicker(
    modifier: Modifier = Modifier,
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    use24HourFormat: Boolean = false,
    visibleRows: Int = 5,
    rowHeight: Dp = 36.dp,
    dividerColor: Color = BloomTheme.colors.primary.copy(alpha = 0.18f),
) {
    val hourRange = if (use24HourFormat) 0..23 else 1..12
    val minuteRange = 0..59

    var internalHour by remember(time, use24HourFormat) {
        mutableStateOf(
            if (use24HourFormat) time.hour else ((time.hour % 12).let { if (it == 0) 12 else it })
        )
    }
    var internalMinute by remember(time) { mutableStateOf(time.minute) }
    var isPm by remember(time, use24HourFormat) {
        mutableStateOf(if (use24HourFormat) false else time.hour >= 12)
    }

    val wheelHeight = rowHeight * visibleRows

    Box(
        modifier = modifier
            .height(wheelHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(BloomTheme.colors.surface)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))

            WheelPickerColumn(
                modifier = Modifier.height(wheelHeight),
                values = hourRange.toList(),
                selectedValue = internalHour,
                onSelected = { value ->
                    internalHour = value
                    onTimeChanged(
                        resolveLocalTime(
                            internalHour,
                            internalMinute,
                            isPm,
                            use24HourFormat
                        )
                    )
                },
                labelFormatter = { it.toString().padStart(2, '0') },
                visibleRows = visibleRows,
                rowHeight = rowHeight,
            )

            Text(
                text = ":",
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            WheelPickerColumn(
                modifier = Modifier.height(wheelHeight),
                values = minuteRange.toList(),
                selectedValue = internalMinute,
                onSelected = { value ->
                    internalMinute = value
                    onTimeChanged(
                        resolveLocalTime(
                            internalHour,
                            internalMinute,
                            isPm,
                            use24HourFormat
                        )
                    )
                },
                labelFormatter = { it.toString().padStart(2, '0') },
                visibleRows = visibleRows,
                rowHeight = rowHeight,
            )

            if (!use24HourFormat) {
                Spacer(modifier = Modifier.width(12.dp))

                AmPmWheel(
                    modifier = Modifier.height(wheelHeight),
                    isPm = isPm,
                    onChange = { pm ->
                        isPm = pm
                        onTimeChanged(
                            resolveLocalTime(
                                internalHour,
                                internalMinute,
                                isPm,
                                use24HourFormat
                            )
                        )
                    },
                    visibleRows = visibleRows,
                    rowHeight = rowHeight,
                )
            }

            Spacer(Modifier.weight(1f))
        }

        // Selection overlay layered above columns
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BloomTheme.colors.primary.copy(alpha = 0.06f))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> WheelPickerColumn(
    modifier: Modifier = Modifier,
    values: List<T>,
    selectedValue: T,
    onSelected: (T) -> Unit,
    labelFormatter: (T) -> String,
    visibleRows: Int,
    rowHeight: Dp,
) {
    val listState = rememberLazyListState(0)
    val coroutineScope = rememberCoroutineScope()
    val snapFlingBehavior = rememberSnapFlingBehavior(listState)
    val haptics = LocalHapticFeedback.current

    // Ensure the selected item is centered on first composition
    LaunchedEffect(values, selectedValue) {
        val index = values.indexOf(selectedValue).coerceAtLeast(0)
        listState.scrollToItem(index)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(
                vertical = rowHeight * (((visibleRows - 1).coerceAtLeast(
                    0
                )) / 2f)
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(values) { item ->
                Row(
                    modifier = Modifier
                        .height(rowHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = labelFormatter(item),
                        style = if (item == selectedValue) BloomTheme.typography.heading else BloomTheme.typography.body,
                        color = if (item == selectedValue) BloomTheme.colors.textColor.primary else BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(48.dp)
                    )
                }
            }
        }

        // When scrolling stops, snap to the closest item and notify
        LaunchedEffect(listState.isScrollInProgress) {
            if (!listState.isScrollInProgress) {
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val closest = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    kotlin.math.abs(itemCenter - viewportCenter)
                }
                val targetIndex = (closest?.index ?: 0).coerceIn(0, values.lastIndex)
                coroutineScope.launch { listState.animateScrollToItem(targetIndex) }
                val target = values[targetIndex]
                if (target != selectedValue) {
                    onSelected(target)
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }
    }
}

@Composable
private fun AmPmWheel(
    modifier: Modifier = Modifier,
    isPm: Boolean,
    onChange: (Boolean) -> Unit,
    visibleRows: Int,
    rowHeight: Dp,
) {
    val options = listOf("AM", "PM")
    val selected = if (isPm) "PM" else "AM"

    WheelPickerColumn(
        modifier = modifier,
        values = options,
        selectedValue = selected,
        onSelected = { label -> onChange(label == "PM") },
        labelFormatter = { it },
        visibleRows = visibleRows,
        rowHeight = rowHeight,
    )
}

private fun resolveLocalTime(
    hourValue: Int,
    minuteValue: Int,
    isPm: Boolean,
    use24HourFormat: Boolean,
): LocalTime {
    val hour24 = if (use24HourFormat) {
        hourValue
    } else {
        val normalized12 = if (hourValue == 12) 0 else hourValue % 12
        if (isPm) normalized12 + 12 else normalized12
    }
    return LocalTime(hour24, minuteValue)
}


