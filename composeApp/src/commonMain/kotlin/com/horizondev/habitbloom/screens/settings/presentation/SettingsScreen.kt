package com.horizondev.habitbloom.screens.settings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designComponents.theme.ThemePickerDialog
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.collectAsEffect
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.appearance
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.data_management
import habitbloom.composeapp.generated.resources.delete
import habitbloom.composeapp.generated.resources.delete_all_data
import habitbloom.composeapp.generated.resources.delete_data_description
import habitbloom.composeapp.generated.resources.delete_data_question
import habitbloom.composeapp.generated.resources.enable_notifications
import habitbloom.composeapp.generated.resources.notifications
import habitbloom.composeapp.generated.resources.settings
import habitbloom.composeapp.generated.resources.settings_appearance_theme
import habitbloom.composeapp.generated.resources.theme_dark
import habitbloom.composeapp.generated.resources.theme_device
import habitbloom.composeapp.generated.resources.theme_light
import org.jetbrains.compose.resources.stringResource

/**
 * Profile screen composable that displays user profile information.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToOnboarding: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is SettingsUiIntent.NavigateToLogin -> {
                // Navigation will be handled by parent NavHost
            }
            is SettingsUiIntent.NavigateToOnboarding -> {
                onNavigateToOnboarding()
            }
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    handleUiEvent: (SettingsUiEvent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BloomTheme.colors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.settings),
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary
                )

                HorizontalDivider(color = BloomTheme.colors.disabled.copy(alpha = 0.5f))

                // Notifications section
                SettingsSection(title = stringResource(Res.string.notifications)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.enable_notifications),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary
                        )
                        BloomSwitch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                handleUiEvent(SettingsUiEvent.ToggleNotifications(enabled))
                            }
                        )
                    }
                }

                HorizontalDivider(color = BloomTheme.colors.disabled.copy(alpha = 0.5f))

                // Appearance section
                SettingsSection(title = stringResource(Res.string.appearance)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { handleUiEvent(SettingsUiEvent.OpenThemeDialog) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_appearance_theme),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.textColor.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (uiState.themeMode) {
                                    ThemeOption.Light -> stringResource(Res.string.theme_light)
                                    ThemeOption.Dark -> stringResource(Res.string.theme_dark)
                                    ThemeOption.Device -> stringResource(Res.string.theme_device)
                                },
                                style = BloomTheme.typography.body,
                                color = BloomTheme.colors.textColor.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = BloomTheme.colors.primary
                            )
                        }
                    }
                }

                HorizontalDivider(color = BloomTheme.colors.disabled.copy(alpha = 0.5f))

                // Data Management Section
                SettingsSection(title = stringResource(Res.string.data_management)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { handleUiEvent(SettingsUiEvent.ShowDeleteDataDialog) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.delete_all_data),
                            style = BloomTheme.typography.body,
                            color = BloomTheme.colors.error
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_all_data),
                            tint = BloomTheme.colors.error
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Show loading indicator if needed
                if (uiState.isLoading) {
                    BloomLoadingAnimation(
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            // Show theme picker dialog when isThemeDialogVisible is true
            if (uiState.isThemeDialogVisible) {
                ThemePickerDialog(
                    currentTheme = uiState.themeMode,
                    onThemeSelected = { themeMode ->
                        handleUiEvent(SettingsUiEvent.SetThemeMode(themeMode))
                    },
                    onDismissRequest = {
                        handleUiEvent(SettingsUiEvent.CloseThemeDialog)
                    }
                )
            }

            // Show delete data confirmation dialog
            if (uiState.showDeleteDataDialog) {
                DeleteDataConfirmationDialog(
                    onDismiss = { handleUiEvent(SettingsUiEvent.DismissDeleteDataDialog) },
                    onConfirm = { handleUiEvent(SettingsUiEvent.ConfirmDeleteData) }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = BloomTheme.typography.subheading,
            color = BloomTheme.colors.textColor.primary
        )
        content()
    }
}

@Composable
private fun DeleteDataConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BloomAlertDialog(
        isShown = true,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = BloomTheme.colors.error,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.delete_data_question),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.heading,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(Res.string.delete_data_description),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete),
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BloomTheme.colors.error,
                    contentColor = BloomTheme.colors.textColor.white
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
                borderStroke = BorderStroke(1.dp, BloomTheme.colors.disabled)
            )
        }
    }
}