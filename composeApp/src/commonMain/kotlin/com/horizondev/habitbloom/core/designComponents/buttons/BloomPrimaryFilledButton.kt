package com.horizondev.habitbloom.core.designComponents.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomPrimaryFilledButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        border = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = BloomTheme.colors.primary,
            contentColor = BloomTheme.colors.textColor.white
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.button,
            color = BloomTheme.colors.textColor.white
        )
    }
}