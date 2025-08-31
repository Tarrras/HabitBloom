package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.layer.setOutline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
fun Modifier.clippedShadow(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor
): Modifier {

    val first = if (elevation > 0.dp) {
        drawWithCache {
            val shadow = obtainGraphicsLayer()
            val clipPath = Path()

            val outline = shape.createOutline(size, layoutDirection, this)
            shadow.run { setOutline(outline); record { } }
            clipPath.run { rewind(); addOutline(outline) }

            onDrawBehind {
                shadow.shadowElevation = elevation.toPx()
                shadow.ambientShadowColor = ambientColor
                shadow.spotShadowColor = spotColor

                clipPath(clipPath, ClipOp.Difference) { drawLayer(shadow) }
            }
        }
    } else {
        this
    }

    return if (clip) first.clip(shape) else first
}

fun String.parseHexColor(): Color {
    val cleanHexString = this.removePrefix("#")
    val argbString = if (cleanHexString.length == 6) {
        "FF$cleanHexString" // Add full alpha if only RGB is provided
    } else {
        cleanHexString
    }
    val colorLong = argbString.toULong(16).toLong() // Convert to unsigned long then to signed long
    return Color(colorLong)
}