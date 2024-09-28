package com.horizondev.habitbloom.core.designComponents.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData
) {
    Snackbar(
        modifier = modifier,
        containerColor = BloomTheme.colors.secondary,
        contentColor = BloomTheme.colors.textColor.primary,
        actionContentColor = BloomTheme.colors.textColor.primary,
        dismissAction = {
            IconButton(
                onClick = { snackbarData.dismiss() },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                        tint = BloomTheme.colors.textColor.primary
                    )
                }
            )
        },
        content = {
            Text(
                text = snackbarData.visuals.message,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary
            )
        }
    )
}