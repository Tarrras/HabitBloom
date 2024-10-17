package com.horizondev.habitbloom.core.designComponents.dialog

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloomAlertDialog(
    modifier: Modifier = Modifier,
    isShown: Boolean = false,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isShown) {
        BasicAlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            BloomSurface(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = AlertDialogDefaults.TonalElevation,
                shadowElevation = 0.dp,
                content = content
            )
        }
    }

}