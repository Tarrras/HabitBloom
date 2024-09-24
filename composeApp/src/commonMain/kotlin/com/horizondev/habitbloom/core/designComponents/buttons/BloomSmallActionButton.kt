package com.horizondev.habitbloom.core.designComponents.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSmallActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        border = BorderStroke(width = 1.dp, color = BloomTheme.colors.primary)
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.primary
        )
    }
}