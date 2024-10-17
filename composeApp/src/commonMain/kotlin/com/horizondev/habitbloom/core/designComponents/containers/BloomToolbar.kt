package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.text.ToolbarTitleText

@Composable
fun BloomToolbar(
    modifier: Modifier = Modifier,
    title: String,
    onBackPressed: (() -> Unit)? = null
) {
    Box(modifier = modifier.padding(16.dp)) {
        onBackPressed?.let { callback ->
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable {
                        callback()
                    }
            )
        }

        ToolbarTitleText(text = title, modifier = Modifier.align(Alignment.Center))
    }
}