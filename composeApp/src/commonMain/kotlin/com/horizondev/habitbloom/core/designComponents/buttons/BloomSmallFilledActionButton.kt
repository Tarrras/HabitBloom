package com.horizondev.habitbloom.core.designComponents.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSmallFilledActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        border = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = BloomTheme.colors.primary,
            contentColor = BloomTheme.colors.textColor.white
        )
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.button,
            color = BloomTheme.colors.textColor.white
        )
    }
}