package com.horizondev.habitbloom.core.designComponents.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.close
import habitbloom.composeapp.generated.resources.settings_appearance_theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ThemePickerDialog(
    currentTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onDismissRequest: () -> Unit
) {
    BloomAlertDialog(
        isShown = true,
        onDismiss = onDismissRequest,
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.settings_appearance_theme),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                ThemePicker(
                    selectedTheme = currentTheme,
                    onThemeSelected = onThemeSelected
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(Res.string.close),
                        style = BloomTheme.typography.button,
                        color = BloomTheme.colors.primary
                    )
                }
            }
        }
    )
} 