package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
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
    // State for tracking drag selection
    var dragStartIndex by remember { mutableIntStateOf(-1) }
    var dragEndIndex by remember { mutableIntStateOf(-1) }
    var isDragging by remember { mutableStateOf(false) }

    // Current selection state (clone of activeDays) that we'll mutate during drag
    val currentSelection = remember(activeDays) { activeDays.toMutableList() }

    // Map to store item positions (using parent-relative coordinates)
    val itemBounds = remember { mutableMapOf<Int, Pair<Float, Float>>() }

    // When drag ends, commit the changes
    LaunchedEffect(isDragging) {
        if (!isDragging && dragStartIndex >= 0 && dragEndIndex >= 0) {
            // Finalize drag and send the updated selection
            onDaysChanged(currentSelection)
            dragStartIndex = -1
            dragEndIndex = -1
        }
    }

    // Remember the initial selection state for determining toggle action
    val initialSelectionState = remember { mutableStateOf<Boolean?>(null) }

    PickerRow(
        modifier = modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput

            detectDragGestures(
                onDragStart = { offset ->
                    // Find which day was initially touched
                    val index = findIndexAtPosition(offset, itemBounds)
                    if (index >= 0) {
                        dragStartIndex = index
                        dragEndIndex = index
                        isDragging = true

                        // Determine the toggle action based on initial state
                        val day = listOfDays[index]
                        val isCurrentlySelected = day in currentSelection
                        initialSelectionState.value = !isCurrentlySelected

                        // Apply toggle to the initial day
                        if (isCurrentlySelected) {
                            currentSelection.remove(day)
                        } else {
                            currentSelection.add(day)
                        }

                        // Immediately update the selection
                        onDaysChanged(currentSelection)
                    }
                },
                onDragEnd = {
                    isDragging = false
                    initialSelectionState.value = null
                },
                onDragCancel = {
                    isDragging = false
                    initialSelectionState.value = null
                },
                onDrag = { change, _ ->
                    if (isDragging) {
                        // Find which day is currently under the pointer
                        val index = findIndexAtPosition(change.position, itemBounds)
                        if (index >= 0 && index != dragEndIndex) {
                            // Get the previous drag end index
                            val previousEndIndex = dragEndIndex
                            dragEndIndex = index

                            // Determine selection direction
                            val startIdx = minOf(dragStartIndex, dragEndIndex)
                            val endIdx = maxOf(dragStartIndex, dragEndIndex)

                            // Get the desired selection state (from the initial toggle action)
                            val targetState =
                                initialSelectionState.value ?: return@detectDragGestures

                            // Create a new selection based on the full range
                            val newSelection = currentSelection.toMutableList()

                            // Ensure all days in range match the target state
                            for (i in startIdx..endIdx) {
                                val day = listOfDays[i]
                                if (targetState && day !in newSelection) {
                                    newSelection.add(day)
                                } else if (!targetState && day in newSelection) {
                                    newSelection.remove(day)
                                }
                            }

                            // Update with new selection
                            if (newSelection != currentSelection) {
                                currentSelection.clear()
                                currentSelection.addAll(newSelection)
                                onDaysChanged(currentSelection)
                            }
                        }
                    }
                }
            )
        },
        backgroundColor = backgroundColor,
        shapeSize = shapeSize
    ) {
        listOfDays.forEachIndexed { index, dayOfWeek ->
            val isActiveDay = dayOfWeek in activeDays
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
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        // Handle click to toggle day selection
                        val updatedSelection = activeDays.toMutableList()
                        if (dayOfWeek in updatedSelection) {
                            updatedSelection.remove(dayOfWeek)
                        } else {
                            updatedSelection.add(dayOfWeek)
                        }
                        onDaysChanged(updatedSelection)
                    }
                    .indication(interactionSource, ripple())
                    .onGloballyPositioned { coordinates ->
                        // Store the position of this item using LOCAL coordinates
                        val bounds = coordinates.boundsInParent()
                        itemBounds[index] = bounds.left to bounds.right
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
 * Helper function to find index of item at a given position
 */
private fun findIndexAtPosition(
    position: Offset,
    itemBounds: Map<Int, Pair<Float, Float>>
): Int {
    val x = position.x

    for ((index, bounds) in itemBounds) {
        val (left, right) = bounds
        if (x >= left && x <= right) {
            return index
        }
    }

    return -1
} 