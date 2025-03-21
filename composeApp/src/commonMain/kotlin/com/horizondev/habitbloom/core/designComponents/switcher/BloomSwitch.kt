package com.horizondev.habitbloom.core.designComponents.switcher

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = BloomTheme.colors.surface,
        checkedTrackColor = BloomTheme.colors.primary,
        uncheckedThumbColor = BloomTheme.colors.textColor.secondary,
        uncheckedTrackColor = BloomTheme.colors.textColor.secondary.copy(alpha = 0.2f)
    ),
    interactionSource: MutableInteractionSource? = null,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}