package com.horizondev.habitbloom.screens.settings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.auth.presentation.signin.SettingsAuthMode
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.common.settings.TimeFormatOption
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.switcher.BloomSwitch
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.platform.appStoreName
import com.horizondev.habitbloom.platform.appVersionName
import com.horizondev.habitbloom.platform.openStoreMainPage
import com.horizondev.habitbloom.utils.clippedShadow
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
import habitbloom.composeapp.generated.resources.ic_google
import habitbloom.composeapp.generated.resources.notifications
import habitbloom.composeapp.generated.resources.settings
import habitbloom.composeapp.generated.resources.settings_about_app
import habitbloom.composeapp.generated.resources.settings_appearance_theme
import habitbloom.composeapp.generated.resources.settings_auth_status_offline
import habitbloom.composeapp.generated.resources.settings_auth_status_synced
import habitbloom.composeapp.generated.resources.settings_auth_unsynced
import habitbloom.composeapp.generated.resources.settings_continue_with_google
import habitbloom.composeapp.generated.resources.settings_create_account_short
import habitbloom.composeapp.generated.resources.settings_delete_data_description_short
import habitbloom.composeapp.generated.resources.settings_guest_profile
import habitbloom.composeapp.generated.resources.settings_personalize_experience
import habitbloom.composeapp.generated.resources.settings_privacy
import habitbloom.composeapp.generated.resources.settings_privacy_subtitle
import habitbloom.composeapp.generated.resources.settings_profile
import habitbloom.composeapp.generated.resources.settings_profile_subtitle
import habitbloom.composeapp.generated.resources.settings_rate_app
import habitbloom.composeapp.generated.resources.settings_reminders_subtitle
import habitbloom.composeapp.generated.resources.settings_sign_in_short
import habitbloom.composeapp.generated.resources.settings_sign_out_account
import habitbloom.composeapp.generated.resources.settings_time_format
import habitbloom.composeapp.generated.resources.settings_version
import habitbloom.composeapp.generated.resources.theme_dark
import habitbloom.composeapp.generated.resources.theme_device
import habitbloom.composeapp.generated.resources.theme_light
import habitbloom.composeapp.generated.resources.time_format_12_hour
import habitbloom.composeapp.generated.resources.time_format_24_hour
import habitbloom.composeapp.generated.resources.time_format_system
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Profile screen composable that displays user profile information.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: (SettingsAuthMode) -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    // Handle navigation
    viewModel.uiIntents.collectAsEffect { intent ->
        when (intent) {
            is SettingsUiIntent.NavigateToOnboarding -> {
                onNavigateToOnboarding()
            }
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
        onNavigateToAuth = onNavigateToAuth
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    handleUiEvent: (SettingsUiEvent) -> Unit,
    onNavigateToAuth: (SettingsAuthMode) -> Unit
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SettingsHeader()
                ProfileCard(
                    profile = uiState.authProfile,
                    onGoogleClick = { handleUiEvent(SettingsUiEvent.SignInWithGoogle) },
                    onSignInClick = { onNavigateToAuth(SettingsAuthMode.SignIn) },
                    onSignUpClick = { onNavigateToAuth(SettingsAuthMode.SignUp) },
                    onSignOutClick = { handleUiEvent(SettingsUiEvent.Logout) }
                )
                AppearanceSection(
                    selectedTheme = uiState.themeMode,
                    selectedTimeFormat = uiState.timeFormat,
                    onThemeSelected = { handleUiEvent(SettingsUiEvent.SetThemeMode(it)) },
                    onTimeFormatSelected = {
                        handleUiEvent(SettingsUiEvent.SetTimeFormat(it))
                    }
                )
                NotificationsSection(
                    notificationsEnabled = uiState.notificationsEnabled,
                    onNotificationsChanged = {
                        handleUiEvent(SettingsUiEvent.ToggleNotifications(it))
                    }
                )
                DataManagementSection(
                    onDeleteClick = { handleUiEvent(SettingsUiEvent.ShowDeleteDataDialog) }
                )
                AboutSection()
            }

            // Show delete data confirmation dialog
            if (uiState.showDeleteDataDialog) {
                DeleteDataConfirmationDialog(
                    onDismiss = { handleUiEvent(SettingsUiEvent.DismissDeleteDataDialog) },
                    onConfirm = { handleUiEvent(SettingsUiEvent.ConfirmDeleteData) }
                )
            }

            if (uiState.isLoading) {
                BloomLoadingAnimation(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings),
            style = BloomTheme.typography.headlineLarge.copy(
                fontSize = 21.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = BloomTheme.colors.textColor.primary
        )
        Text(
            text = stringResource(Res.string.settings_personalize_experience),
            style = BloomTheme.typography.bodyMedium,
            color = SettingsMutedText
        )
    }
}

@Composable
private fun ProfileCard(
    profile: SettingsAuthProfileUiState,
    onGoogleClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    SettingsCard(
        contentPadding = 18.dp,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProfileAvatar(profile = profile)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = profileTitle(profile),
                        modifier = Modifier.weight(1f, fill = false),
                        style = BloomTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = BloomTheme.colors.textColor.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    ProfileStatusBadge(isAuthenticated = profile.isAuthenticated)
                }
                Text(
                    text = profileSubtitle(profile),
                    style = BloomTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    ),
                    color = if (profile.isAuthenticated) SettingsMutedText else SettingsWarning,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (profile.isAuthenticated) {
            ProfileActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.settings_sign_out_account),
                icon = Icons.AutoMirrored.Outlined.Logout,
                contentColor = SettingsDanger,
                backgroundColor = SettingsDanger.copy(alpha = 0.1f),
                borderColor = SettingsDanger.copy(alpha = 0.2f),
                onClick = onSignOutClick,
                horizontalArrangement = Arrangement.Start
            )
        } else {
            val isLightSurface = BloomTheme.colors.surface.luminance() > 0.5f
            ProfileActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.settings_continue_with_google),
                contentColor = BloomTheme.colors.textColor.primary,
                backgroundColor = if (isLightSurface) {
                    BloomTheme.colors.cardMuted
                } else {
                    BloomTheme.colors.surface
                },
                borderColor = if (isLightSurface) {
                    BloomTheme.colors.border
                } else {
                    BloomTheme.colors.border.copy(alpha = 0.5f)
                },
                onClick = onGoogleClick,
                leadingContent = { GoogleGlyph() }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.settings_sign_in_short),
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentColor = BloomTheme.colors.primary,
                    backgroundColor = BloomTheme.colors.primary.copy(alpha = 0.1f),
                    borderColor = BloomTheme.colors.primary.copy(alpha = 0.2f),
                    onClick = onSignInClick
                )
                ProfileActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.settings_create_account_short),
                    icon = Icons.Outlined.PersonAdd,
                    contentColor = BloomTheme.colors.primaryForeground,
                    backgroundColor = BloomTheme.colors.primary,
                    borderColor = Color.Transparent,
                    onClick = onSignUpClick
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(profile: SettingsAuthProfileUiState) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(56.dp)
            .then(
                if (profile.isAuthenticated) {
                    Modifier.clippedShadow(elevation = 10.dp, shape = shape, clip = false)
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(
                if (profile.isAuthenticated) {
                    Brush.linearGradient(
                        listOf(BloomTheme.colors.primary, BloomTheme.colors.primaryVariant)
                    )
                } else {
                    Brush.linearGradient(
                        listOf(BloomTheme.colors.surfaceVariant, BloomTheme.colors.surfaceVariant)
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (profile.isAuthenticated) {
                    Color.Transparent
                } else {
                    BloomTheme.colors.border.copy(alpha = 0.4f)
                },
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (profile.isAuthenticated) {
            Text(
                text = profileInitials(profile.title),
                fontSize = 21.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BloomTheme.colors.primaryForeground
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = SettingsMutedText
            )
        }
    }
}

@Composable
private fun ProfileStatusBadge(isAuthenticated: Boolean) {
    val backgroundColor = if (isAuthenticated) {
        BloomTheme.colors.primary.copy(alpha = 0.15f)
    } else {
        BloomTheme.colors.surfaceVariant
    }
    val textColor = if (isAuthenticated) BloomTheme.colors.primary else SettingsMutedText
    val text = if (isAuthenticated) {
        stringResource(Res.string.settings_auth_status_synced)
    } else {
        stringResource(Res.string.settings_auth_status_offline)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(backgroundColor)
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
private fun ProfileActionButton(
    modifier: Modifier = Modifier,
    text: String,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.width(10.dp))
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = BloomTheme.typography.button.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GoogleGlyph() {
    Image(
        painter = painterResource(Res.drawable.ic_google),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun profileTitle(profile: SettingsAuthProfileUiState): String {
    return if (profile.isAuthenticated) {
        profile.title.ifBlank { stringResource(Res.string.settings_profile) }
    } else {
        stringResource(Res.string.settings_guest_profile)
    }
}

@Composable
private fun profileSubtitle(profile: SettingsAuthProfileUiState): String {
    return if (profile.isAuthenticated) {
        profile.subtitle.ifBlank { stringResource(Res.string.settings_profile_subtitle) }
    } else {
        stringResource(Res.string.settings_auth_unsynced)
    }
}

private fun profileInitials(title: String): String {
    val source = title.substringBefore('@').trim()
    val initials = source
        .split(' ', '.', '-', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercaseChar().toString() }

    return initials.ifBlank {
        source.take(2).uppercase().ifBlank { "HB" }
    }
}

@Composable
private fun AppearanceSection(
    selectedTheme: ThemeOption,
    selectedTimeFormat: TimeFormatOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onTimeFormatSelected: (TimeFormatOption) -> Unit
) {
    SettingsSection(title = stringResource(Res.string.appearance)) {
        SettingsCard(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsIconBox(
                    icon = Icons.Outlined.Palette,
                    backgroundColor = BloomTheme.colors.primary.copy(alpha = 0.1f),
                    tint = BloomTheme.colors.primary
                )
                Text(
                    text = stringResource(Res.string.settings_appearance_theme),
                    style = BloomTheme.typography.titleSmall,
                    color = BloomTheme.colors.textColor.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.theme_light),
                    icon = Icons.Outlined.LightMode,
                    selected = selectedTheme == ThemeOption.Light,
                    onClick = { onThemeSelected(ThemeOption.Light) }
                )
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.theme_dark),
                    icon = Icons.Outlined.DarkMode,
                    selected = selectedTheme == ThemeOption.Dark,
                    onClick = { onThemeSelected(ThemeOption.Dark) }
                )
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.theme_device),
                    icon = Icons.Outlined.Computer,
                    selected = selectedTheme == ThemeOption.Device,
                    onClick = { onThemeSelected(ThemeOption.Device) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsIconBox(
                    icon = Icons.Outlined.Schedule,
                    backgroundColor = BloomTheme.colors.primary.copy(alpha = 0.1f),
                    tint = BloomTheme.colors.primary
                )
                Text(
                    text = stringResource(Res.string.settings_time_format),
                    style = BloomTheme.typography.titleSmall,
                    color = BloomTheme.colors.textColor.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.time_format_system),
                    icon = Icons.Outlined.Computer,
                    selected = selectedTimeFormat == TimeFormatOption.System,
                    onClick = { onTimeFormatSelected(TimeFormatOption.System) }
                )
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.time_format_12_hour),
                    icon = Icons.Outlined.Schedule,
                    selected = selectedTimeFormat == TimeFormatOption.TwelveHour,
                    onClick = { onTimeFormatSelected(TimeFormatOption.TwelveHour) }
                )
                ThemeChoiceButton(
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.time_format_24_hour),
                    icon = Icons.Outlined.Schedule,
                    selected = selectedTimeFormat == TimeFormatOption.TwentyFourHour,
                    onClick = { onTimeFormatSelected(TimeFormatOption.TwentyFourHour) }
                )
            }
        }
    }
}

@Composable
private fun NotificationsSection(
    notificationsEnabled: Boolean,
    onNotificationsChanged: (Boolean) -> Unit
) {
    SettingsSection(title = stringResource(Res.string.notifications)) {
        SettingsCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsIconBox(
                    icon = Icons.Outlined.NotificationsNone,
                    backgroundColor = BloomTheme.colors.primary.copy(alpha = 0.1f),
                    tint = BloomTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.enable_notifications),
                        style = BloomTheme.typography.titleSmall,
                        color = BloomTheme.colors.textColor.primary
                    )
                    Text(
                        text = stringResource(Res.string.settings_reminders_subtitle),
                        style = BloomTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 20.sp
                        ),
                        color = SettingsMutedText
                    )
                }
                BloomSwitch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChanged
                )
            }
        }
    }
}

@Composable
private fun DataManagementSection(
    onDeleteClick: () -> Unit
) {
    SettingsSection(title = stringResource(Res.string.data_management)) {
        SettingsCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SettingsDanger.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = SettingsDanger.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onDeleteClick)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsIconBox(
                    icon = Icons.Outlined.Delete,
                    backgroundColor = SettingsDanger.copy(alpha = 0.2f),
                    tint = SettingsDanger
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.delete_all_data),
                        style = BloomTheme.typography.titleSmall,
                        color = SettingsDanger
                    )
                    Text(
                        text = stringResource(Res.string.settings_delete_data_description_short),
                        style = BloomTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 20.sp
                        ),
                        color = SettingsDanger.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSection() {
    SettingsSection(title = stringResource(Res.string.settings_about_app)) {
        SettingsCard(
            contentPadding = 0.dp,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            AboutRow(
                icon = Icons.Outlined.StarBorder,
                title = stringResource(Res.string.settings_rate_app),
                subtitle = appStoreName,
                showDivider = true,
                onClick = ::openStoreMainPage
            )
            AboutRow(
                icon = Icons.Outlined.Shield,
                title = stringResource(Res.string.settings_privacy),
                subtitle = stringResource(Res.string.settings_privacy_subtitle),
                showDivider = true
            )
            AboutRow(
                icon = Icons.Outlined.Info,
                title = stringResource(Res.string.settings_version),
                subtitle = appVersionName,
                showDivider = false
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title.uppercase(),
            modifier = Modifier.padding(start = 4.dp),
            style = BloomTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            color = SettingsMutedText
        )
        content()
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 16.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clippedShadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(BloomTheme.colors.surface.copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = SettingsMutedText.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(contentPadding),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
private fun ThemeChoiceButton(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        BloomTheme.colors.primary.copy(alpha = 0.5f)
    } else {
        BloomTheme.colors.surfaceVariant.copy(alpha = 0.3f)
    }
    val backgroundColor = if (selected) {
        BloomTheme.colors.primary.copy(alpha = 0.15f)
    } else {
        BloomTheme.colors.surfaceVariant.copy(alpha = 0.4f)
    }
    val contentColor = if (selected) BloomTheme.colors.primary else SettingsMutedText

    Column(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp),
            tint = contentColor
        )
        Text(
            text = label,
            style = BloomTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SettingsIconBox(
    icon: ImageVector,
    backgroundColor: Color,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tint
        )
    }
}

@Composable
private fun AboutRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showDivider: Boolean,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .let { modifier ->
            if (onClick != null) modifier.clickable(onClick = onClick) else modifier
        }
        .padding(horizontal = 16.dp)

    Box {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsIconBox(
                icon = icon,
                backgroundColor = BloomTheme.colors.surfaceVariant.copy(alpha = 0.6f),
                tint = SettingsMutedText
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = BloomTheme.typography.titleSmall,
                    color = BloomTheme.colors.textColor.primary
                )
                Text(
                    text = subtitle,
                    style = BloomTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    ),
                    color = SettingsMutedText
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = SettingsMutedText.copy(alpha = 0.65f)
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BloomTheme.colors.surfaceVariant.copy(alpha = 0.2f))
            )
        }
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

private val SettingsMutedText = Color(0xFF94A3B8)
private val SettingsWarning = Color(0xFFEAB308)
private val SettingsDanger = Color(0xFFF87171)
