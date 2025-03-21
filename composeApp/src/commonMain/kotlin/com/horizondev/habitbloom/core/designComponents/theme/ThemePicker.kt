package com.horizondev.habitbloom.core.designComponents.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.common.settings.ThemeOption
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.theme_dark
import habitbloom.composeapp.generated.resources.theme_device
import habitbloom.composeapp.generated.resources.theme_light
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThemePicker(
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeOption.values().forEach { theme ->
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { onThemeSelected(theme) }
                )
                Text(
                    text = when (theme) {
                        ThemeOption.Light -> stringResource(Res.string.theme_light)
                        ThemeOption.Dark -> stringResource(Res.string.theme_dark)
                        ThemeOption.Device -> stringResource(Res.string.theme_device)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
} 