package com.horizondev.habitbloom.screens.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.horizondev.habitbloom.utils.collectAsEffect

/**
 * Profile screen composable that displays user profile information.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is ProfileUiIntent.NavigateToLogin -> {
                // Navigation will be handled by parent NavHost
            }
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        modifier = modifier
    )
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    handleUiEvent: (ProfileUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    /*    Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(Res.string.profile),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BloomTheme.colors.primary)
                    }
                }
                uiState.profile == null -> {
                    Text(
                        text = "No profile information available",
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    // Profile avatar
                    ProfileAvatar(
                        avatarUrl = uiState.profile.avatarUrl,
                        userName = uiState.profile.name,
                        onAvatarClick = { *//* Handle avatar click *//* },
                    modifier = Modifier
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // User name
                Text(
                    text = uiState.profile.name,
                    style = BloomTheme.typography.title,
                    color = BloomTheme.colors.textColor.primary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Profile stats
                // TODO: Add profile statistics here
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Logout button
                BloomPrimaryFilledButton(
                    text = stringResource(Res.string.logout),
                    onClick = { handleUiEvent(ProfileUiEvent.Logout) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }*/
}