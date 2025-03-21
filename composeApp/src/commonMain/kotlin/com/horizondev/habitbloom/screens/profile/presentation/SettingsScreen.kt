package com.horizondev.habitbloom.screens.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.collectAsEffect
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

/**
 * Profile screen composable that displays user profile information.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is SettingsUiIntent.NavigateToLogin -> {
                // Navigation will be handled by parent NavHost
            }
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        modifier = modifier
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    handleUiEvent: (SettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = stringResource(Res.string.settings),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary
        )

        // Notifications Section
        SettingsSection(
            title = "Notifications",
            content = {
                SettingsSwitch(
                    title = "Enable Notifications",
                    description = "Get reminders for your habits",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { handleUiEvent(SettingsUiEvent.ToggleNotifications(it)) }
                )
            }
        )

        // Appearance Section
        SettingsSection(
            title = "Appearance",
            content = {
                SettingsSwitch(
                    title = "Dark Mode",
                    description = "Use dark theme",
                    checked = uiState.darkModeEnabled,
                    onCheckedChange = { handleUiEvent(SettingsUiEvent.ToggleDarkMode(it)) }
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BloomLoadingAnimation(
                modifier = Modifier.size(100.dp)
            )
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
private fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary
            )
            Text(
                text = description,
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.secondary
            )
        }
        BloomSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}