package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import kotlin.math.ceil

@Composable
fun BloomGrid(
    modifier: Modifier = Modifier,
    columns: Int,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable () -> Unit
) {
    require(columns > 0) { "Columns must be > 0" }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.toPx().toInt()
        val vSpacingPx = verticalSpacing.toPx().toInt()
        val itemConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        // Measure children
        val placeables = measurables.map { measurable ->
            measurable.measure(itemConstraints)
        }

        // Calculate grid dimensions
        val rowCount = ceil(placeables.size / columns.toFloat()).toInt()
        val columnWidth = (constraints.maxWidth - (hSpacingPx * (columns - 1))) / columns
        val itemHeight = placeables.firstOrNull()?.height ?: 0
        val rowHeight = itemHeight + vSpacingPx

        val gridHeight = rowCount * rowHeight - vSpacingPx

        layout(constraints.maxWidth, gridHeight) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / columns
                val column = index % columns

                val x = column * (columnWidth + hSpacingPx)
                val y = row * (itemHeight + vSpacingPx)

                placeable.placeRelative(x = x, y = y)
            }
        }
    }
}