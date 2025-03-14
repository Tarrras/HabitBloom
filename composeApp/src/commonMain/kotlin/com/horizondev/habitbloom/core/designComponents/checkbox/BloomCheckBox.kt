package com.horizondev.habitbloom.core.designComponents.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomCheckBox(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    iconSize: Dp = 16.dp,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    shape: Shape = CircleShape
) {
    val backgroundColor by animateColorAsState(
        if (checked) BloomTheme.colors.primary else BloomTheme.colors.surface
    )

    val borderColor by animateColorAsState(
        if (checked) BloomTheme.colors.primary else BloomTheme.colors.disabled
    )

    Box(
        modifier = modifier
            .size(size)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(color = backgroundColor, shape = shape)
            .clip(shape = shape)
            .triStateToggleable(
                state = ToggleableState(checked),
                onClick = {
                    onCheckedChange(!checked)
                },
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = size
                )
            )
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                tint = BloomTheme.colors.surface,
                modifier = Modifier.size(iconSize).align(Alignment.Center),
                contentDescription = "checked",
            )
        }
    }
}