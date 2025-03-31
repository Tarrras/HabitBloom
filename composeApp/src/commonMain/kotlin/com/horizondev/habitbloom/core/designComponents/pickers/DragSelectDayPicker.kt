package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getShortTitle
import kotlinx.datetime.DayOfWeek

/**
 * An enhanced day picker with drag selection support for faster multi-day selection.
 *
 * @param activeDays List of currently active days
 * @param onDaysChanged Callback when days selection changes
 * @param shapeSize Size of the day buttons
 * @param backgroundColor Background color of the picker
 * @param listOfDays List of days to show (defaults to all days of week)
 * @param enabled Whether the component is enabled
 */
@Composable
fun DragSelectDayPicker(
    modifier: Modifier = Modifier,
    activeDays: List<DayOfWeek>,
    onDaysChanged: (List<DayOfWeek>) -> Unit,
    shapeSize: Dp = 36.dp,
    backgroundColor: Color = BloomTheme.colors.background,
    listOfDays: List<DayOfWeek> = DayOfWeek.entries,
    enabled: Boolean = true
) {
    // Track drag operation
    var isDragging by remember { mutableStateOf(false) }

    // Track current selection - initialized with activeDays
    val selectedDays = remember(activeDays) {
        mutableStateListOf<DayOfWeek>().apply {
            addAll(activeDays)
        } 
    }

    // Keep track of last touched day to avoid repeat updates
    var lastTouchedDay by remember { mutableStateOf<DayOfWeek?>(null) }

    // The action we're performing (add or remove days)
    var isAddingDays by remember { mutableStateOf(true) }

    // Store bounds for each day cell by index for more reliable lookup
    val dayBounds = remember { mutableStateMapOf<Int, Rect>() }
    
    PickerRow(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                detectDragGestures(
                    onDragStart = { initialPosition ->
                        isDragging = true

                        // Find which day we initially touched
                        val index = getDayIndexAtPosition(initialPosition, dayBounds)
                        if (index != -1 && index < listOfDays.size) {
                            val day = listOfDays[index]
                            lastTouchedDay = day

                            // Determine if we're adding or removing days
                            isAddingDays = day !in selectedDays

                            // Toggle the initial day
                            val newSelection = selectedDays.toMutableList()
                            if (isAddingDays) {
                                newSelection.add(day)
                            } else {
                                newSelection.remove(day)
                            }

                            selectedDays.clear()
                            selectedDays.addAll(newSelection)
                            onDaysChanged(newSelection) // Always notify
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        lastTouchedDay = null
                    },
                    onDragCancel = {
                        isDragging = false
                        lastTouchedDay = null
                    },
                    onDrag = { change, _ ->
                        // Important: Use the current drag position
                        val index = getDayIndexAtPosition(change.position, dayBounds)

                        // Only process if we've moved to a valid day that's different from the last one
                        if (index != -1 && index < listOfDays.size) {
                            val day = listOfDays[index]

                            // Skip if we're still on the same day
                            if (day == lastTouchedDay) return@detectDragGestures

                            // Update last touched day
                            lastTouchedDay = day

                            // Apply our action (add or remove) to this day
                            val newSelection = selectedDays.toMutableList()

                            if (isAddingDays) {
                                // We're in "add" mode - make sure this day is added
                                if (day !in newSelection) {
                                    newSelection.add(day)
                                }
                            } else {
                                // We're in "remove" mode - make sure this day is removed
                                newSelection.remove(day)
                            }

                            // Always update the selection and notify
                            selectedDays.clear()
                            selectedDays.addAll(newSelection)
                            onDaysChanged(newSelection) // Always notify
                        }
                    }
                )
            },
        backgroundColor = backgroundColor,
        shapeSize = shapeSize
    ) {
        listOfDays.forEachIndexed { index, dayOfWeek ->
            val isActiveDay = dayOfWeek in selectedDays
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = shapeSize, bottomStart = shapeSize)
                listOfDays.lastIndex -> RoundedCornerShape(
                    topEnd = shapeSize,
                    bottomEnd = shapeSize
                )
                else -> RectangleShape
            }

            // Interaction source for ripple effect
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isActiveDay) BloomTheme.colors.primary else Color.Transparent,
                        shape = shape
                    )
                    .fillMaxHeight()
                    .clip(shape)
                    .clickable(
                        enabled = enabled && !isDragging,
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        // Handle single click
                        val newSelection = selectedDays.toMutableList()
                        if (dayOfWeek in newSelection) {
                            newSelection.remove(dayOfWeek)
                        } else {
                            newSelection.add(dayOfWeek)
                        }

                        selectedDays.clear()
                        selectedDays.addAll(newSelection)
                        onDaysChanged(newSelection)
                    }
                    .indication(interactionSource, ripple())
                    .onGloballyPositioned { coordinates ->
                        // Save the bounds of this day cell for hit-testing by index
                        dayBounds[index] = coordinates.boundsInRoot()
                    }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getShortTitle(),
                    color = if (isActiveDay) BloomTheme.colors.textColor.white
                    else BloomTheme.colors.textColor.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Find the index of the day at the given position
 */
private fun getDayIndexAtPosition(position: Offset, bounds: Map<Int, Rect>): Int {
    for ((index, rect) in bounds) {
        if (position.x >= rect.left &&
            position.x <= rect.right &&
            position.y >= rect.top &&
            position.y <= rect.bottom
        ) {
            return index
        }
    }
    return -1
} 