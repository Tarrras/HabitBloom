package com.horizondev.habitbloom.core.designComponents.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarTitleText(
    modifier: Modifier = Modifier,
    text: String = stringResource(Res.string.app_name)
) {
    Text(
        text = text,
        style = BloomTheme.typography.heading,
        color = BloomTheme.colors.textColor.primary,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}