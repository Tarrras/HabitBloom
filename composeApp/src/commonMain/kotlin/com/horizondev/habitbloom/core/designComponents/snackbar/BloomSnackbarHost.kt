package com.horizondev.habitbloom.core.designComponents.snackbar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

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