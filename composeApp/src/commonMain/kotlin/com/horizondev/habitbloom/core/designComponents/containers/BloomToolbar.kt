package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.text.ToolbarTitleText
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomToolbar(
    modifier: Modifier = Modifier,
    title: String,
    onBackPressed: (() -> Unit)? = null,
    menuItems: List<Pair<String, () -> Unit>> = emptyList()
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

        if (menuItems.isNotEmpty()) {
            var showMenu by remember { mutableStateOf(false) }

            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showMenu = true
                        }
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = BloomTheme.colors.surface
                ) {
                    menuItems.forEach { (label, action) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label, style = BloomTheme.typography.body,
                                    color = BloomTheme.colors.textColor.primary,
                                )
                            },
                            onClick = {
                                showMenu = false
                                action()
                            }
                        )
                    }
                }
            }
        }
    }
}