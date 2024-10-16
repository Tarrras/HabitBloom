package com.horizondev.habitbloom.core.designComponents.snackbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData
) {
    val state = snackbarData.visuals.actionLabel?.let {
        BloomSnackbarState.valueOf(it)
    } ?: BloomSnackbarState.Warning

    Snackbar(
        modifier = modifier,
        containerColor = state.snackBarBackgroundColor(),
        dismissAction = {
            IconButton(
                onClick = { snackbarData.dismiss() },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                        tint = state.snackBarTextColor()
                    )
                }
            )
        },
        content = {
            Text(
                text = snackbarData.visuals.message,
                style = BloomTheme.typography.body,
                color = state.snackBarTextColor()
            )
        }
    )
}

enum class BloomSnackbarState {
    Success,
    Warning,
    Error
}

@Composable
fun BloomSnackbarState.snackBarBackgroundColor() = when (this) {
    BloomSnackbarState.Success -> BloomTheme.colors.success
    BloomSnackbarState.Warning -> BloomTheme.colors.secondary
    BloomSnackbarState.Error -> BloomTheme.colors.error
}

@Composable
fun BloomSnackbarState.snackBarTextColor() = when (this) {
    BloomSnackbarState.Success -> BloomTheme.colors.textColor.white
    BloomSnackbarState.Warning -> BloomTheme.colors.textColor.primary
    BloomSnackbarState.Error -> BloomTheme.colors.textColor.white
}