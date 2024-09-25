package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.annotation.IntRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import kotlin.math.roundToInt

@Composable
fun BloomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0)
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors(
        thumbColor = BloomTheme.colors.primary,
        activeTrackColor = BloomTheme.colors.primary,
        inactiveTrackColor = BloomTheme.colors.background,
        inactiveTickColor = Color.Transparent,
        activeTickColor = Color.Transparent
    )
) {
    Column(
        modifier = modifier
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            interactionSource = interactionSource,
            colors = colors
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -ThumbRadius)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = valueRange.start.roundToInt().toString(),
                color = BloomTheme.colors.primary,
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = valueRange.endInclusive.roundToInt().toString(),
                color = BloomTheme.colors.primary,
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

private val ThumbRadius = 10.dp