package com.horizondev.habitbloom.core.designComponents.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BloomSnackbarHost(
    modifier: Modifier = Modifier,
    snackBarState: SnackbarHostState
) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackBarState,
        snackbar = { snackbarData ->
            BloomSnackbar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                snackbarData = snackbarData
            )
        }
    )
}