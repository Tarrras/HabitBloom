package com.horizondev.habitbloom.auth.presentation.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.settings_reset_password_confirm
import habitbloom.composeapp.generated.resources.settings_reset_password_confirm_placeholder
import habitbloom.composeapp.generated.resources.settings_reset_password_new
import habitbloom.composeapp.generated.resources.settings_reset_password_placeholder
import habitbloom.composeapp.generated.resources.settings_reset_password_submit
import habitbloom.composeapp.generated.resources.settings_reset_password_subtitle
import habitbloom.composeapp.generated.resources.settings_reset_password_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsResetPasswordScreen(
    viewModel: SettingsResetPasswordViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onNavigateBack()
        }
    }

    SettingsResetPasswordContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPasswordChanged = {
            viewModel.handleUiEvent(SettingsResetPasswordUiEvent.UpdatePassword(it))
        },
        onConfirmPasswordChanged = {
            viewModel.handleUiEvent(SettingsResetPasswordUiEvent.UpdateConfirmPassword(it))
        },
        onSubmit = {
            viewModel.handleUiEvent(SettingsResetPasswordUiEvent.Submit)
        }
    )
}

@Composable
private fun SettingsResetPasswordContent(
    uiState: SettingsResetPasswordUiState,
    onNavigateBack: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BloomTheme.colors.surfaceVariant)
                    .clickable(onClick = onNavigateBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.cancel),
                    modifier = Modifier.size(22.dp),
                    tint = BloomTheme.colors.textColor.secondary
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BloomTheme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = BloomTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.settings_reset_password_title),
                style = BloomTheme.typography.displaySmall,
                color = BloomTheme.colors.textColor.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(Res.string.settings_reset_password_subtitle),
                style = BloomTheme.typography.bodyLarge,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            ResetPasswordTextField(
                label = stringResource(Res.string.settings_reset_password_new),
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = stringResource(Res.string.settings_reset_password_placeholder),
                enabled = !uiState.isLoading,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChanged = { passwordVisible = it },
                isError = uiState.error != null
            )

            Spacer(modifier = Modifier.height(14.dp))

            ResetPasswordTextField(
                label = stringResource(Res.string.settings_reset_password_confirm),
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                placeholder = stringResource(Res.string.settings_reset_password_confirm_placeholder),
                enabled = !uiState.isLoading,
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityChanged = { confirmPasswordVisible = it },
                isError = uiState.error != null
            )

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    style = BloomTheme.typography.bodySmall,
                    color = BloomTheme.colors.destructive
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            BloomPrimaryFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                text = stringResource(Res.string.settings_reset_password_submit),
                onClick = onSubmit,
                enabled = !uiState.isLoading
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

@Composable
private fun ResetPasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    passwordVisible: Boolean,
    onPasswordVisibilityChanged: (Boolean) -> Unit,
    isError: Boolean
) {
    BloomTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = BloomTheme.typography.bodyLarge.copy(
            color = BloomTheme.colors.textColor.primary
        ),
        placeholderText = placeholder,
        title = label,
        isError = isError,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        shape = RoundedCornerShape(13.dp),
        trailingIcon = {
            Icon(
                imageVector = if (passwordVisible) {
                    Icons.Outlined.VisibilityOff
                } else {
                    Icons.Outlined.Visibility
                },
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(
                        enabled = enabled,
                        onClick = { onPasswordVisibilityChanged(!passwordVisible) }
                    ),
                tint = BloomTheme.colors.textColor.secondary
            )
        }
    )
}
