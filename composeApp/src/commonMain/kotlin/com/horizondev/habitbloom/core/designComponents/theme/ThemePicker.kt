package com.horizondev.habitbloom.core.designComponents.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designSystem.BloomTheme
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
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ThemeOption.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { onThemeSelected(theme) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = BloomTheme.colors.primary,
                        unselectedColor = BloomTheme.colors.textColor.secondary,
                        disabledSelectedColor = BloomTheme.colors.textColor.secondary.copy(alpha = 0.6f),
                        disabledUnselectedColor = BloomTheme.colors.textColor.secondary.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = when (theme) {
                        ThemeOption.Light -> stringResource(Res.string.theme_light)
                        ThemeOption.Dark -> stringResource(Res.string.theme_dark)
                        ThemeOption.Device -> stringResource(Res.string.theme_device)
                    },
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (theme != ThemeOption.entries.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
} 