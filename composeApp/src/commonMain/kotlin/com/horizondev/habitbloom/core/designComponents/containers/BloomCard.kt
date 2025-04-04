package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: CardElevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = BloomTheme.colors.surface,
        contentColor = Color.Unspecified
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = RoundedCornerShape(12.dp), // Rounded corners for a soft appearance
        colors = colors,
        onClick = onClick,
        interactionSource = interactionSource,
        enabled = enabled,
        border = border,
        content = content
    )
}