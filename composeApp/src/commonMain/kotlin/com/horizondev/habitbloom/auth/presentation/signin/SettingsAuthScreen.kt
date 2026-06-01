package com.horizondev.habitbloom.auth.presentation.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.settings_auth_create_password
import habitbloom.composeapp.generated.resources.settings_auth_enter_email
import habitbloom.composeapp.generated.resources.settings_auth_enter_name
import habitbloom.composeapp.generated.resources.settings_auth_enter_password
import habitbloom.composeapp.generated.resources.settings_auth_forgot_password
import habitbloom.composeapp.generated.resources.settings_auth_login_footer
import habitbloom.composeapp.generated.resources.settings_auth_name
import habitbloom.composeapp.generated.resources.settings_auth_sign_in_subtitle
import habitbloom.composeapp.generated.resources.settings_auth_sign_in_title
import habitbloom.composeapp.generated.resources.settings_auth_sign_up_submit
import habitbloom.composeapp.generated.resources.settings_auth_sign_up_subtitle
import habitbloom.composeapp.generated.resources.settings_auth_sign_up_title
import habitbloom.composeapp.generated.resources.settings_auth_terms_after_rules
import habitbloom.composeapp.generated.resources.settings_auth_terms_and
import habitbloom.composeapp.generated.resources.settings_auth_terms_before
import habitbloom.composeapp.generated.resources.settings_auth_terms_privacy
import habitbloom.composeapp.generated.resources.settings_auth_terms_rules
import habitbloom.composeapp.generated.resources.settings_auth_verify_email_action
import habitbloom.composeapp.generated.resources.settings_auth_verify_email_message
import habitbloom.composeapp.generated.resources.settings_auth_verify_email_title
import habitbloom.composeapp.generated.resources.settings_auth_welcome_back
import habitbloom.composeapp.generated.resources.settings_create_account_short
import habitbloom.composeapp.generated.resources.settings_email
import habitbloom.composeapp.generated.resources.settings_password
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsAuthScreen(
    viewModel: SettingsAuthViewModel,
    initialMode: SettingsAuthMode,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(initialMode) {
        viewModel.handleUiEvent(SettingsAuthUiEvent.SetMode(initialMode))
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateBack()
        }
    }

    AuthFlowScreenContent(
        uiState = uiState,
        onDismiss = onNavigateBack,
        onNameChanged = { viewModel.handleUiEvent(SettingsAuthUiEvent.UpdateName(it)) },
        onEmailChanged = { viewModel.handleUiEvent(SettingsAuthUiEvent.UpdateEmail(it)) },
        onPasswordChanged = { viewModel.handleUiEvent(SettingsAuthUiEvent.UpdatePassword(it)) },
        onSubmit = { viewModel.handleUiEvent(SettingsAuthUiEvent.SubmitEmailAuth) },
        onResetPassword = { viewModel.handleUiEvent(SettingsAuthUiEvent.ResetPassword) },
        onSwitchToSignUp = { viewModel.handleUiEvent(SettingsAuthUiEvent.SetMode(SettingsAuthMode.SignUp)) },
        onDismissEmailVerificationSheet = {
            viewModel.handleUiEvent(SettingsAuthUiEvent.DismissEmailVerificationSheet)
            onNavigateBack()
        }
    )
}

@Composable
private fun AuthFlowScreenContent(
    uiState: SettingsAuthUiState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onResetPassword: () -> Unit,
    onSwitchToSignUp: () -> Unit,
    onDismissEmailVerificationSheet: () -> Unit
) {
    val isSignIn = uiState.authMode == SettingsAuthMode.SignIn
    var passwordVisible by remember(uiState.authMode) { mutableStateOf(false) }

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
                    .clickable(onClick = onDismiss),
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

            AuthFlowHeroIcon(isSignIn = isSignIn)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isSignIn) {
                    stringResource(Res.string.settings_auth_welcome_back)
                } else {
                    stringResource(Res.string.settings_auth_sign_up_title)
                },
                style = BloomTheme.typography.displaySmall,
                color = BloomTheme.colors.textColor.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSignIn) {
                    stringResource(Res.string.settings_auth_sign_in_subtitle)
                } else {
                    stringResource(Res.string.settings_auth_sign_up_subtitle)
                },
                style = BloomTheme.typography.bodyLarge,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(if (isSignIn) 36.dp else 30.dp))

            if (!isSignIn) {
                AuthFlowTextField(
                    label = stringResource(Res.string.settings_auth_name),
                    value = uiState.authName,
                    onValueChange = onNameChanged,
                    placeholder = stringResource(Res.string.settings_auth_enter_name),
                    enabled = !uiState.isAuthLoading,
                    isError = uiState.authError != null && uiState.authName.isBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            AuthFlowTextField(
                label = stringResource(Res.string.settings_email),
                value = uiState.authEmail,
                onValueChange = onEmailChanged,
                placeholder = stringResource(Res.string.settings_auth_enter_email),
                enabled = !uiState.isAuthLoading,
                isError = uiState.authError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthFlowTextField(
                label = stringResource(Res.string.settings_password),
                value = uiState.authPassword,
                onValueChange = onPasswordChanged,
                placeholder = if (isSignIn) {
                    stringResource(Res.string.settings_auth_enter_password)
                } else {
                    stringResource(Res.string.settings_auth_create_password)
                },
                enabled = !uiState.isAuthLoading,
                isError = uiState.authError != null,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChanged = { passwordVisible = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            if (isSignIn) {
                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = stringResource(Res.string.settings_auth_forgot_password),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable(
                            enabled = !uiState.isAuthLoading,
                            onClick = onResetPassword
                        ),
                    style = BloomTheme.typography.labelMedium,
                    color = BloomTheme.colors.primary,
                    textAlign = TextAlign.End
                )
            }

            uiState.authError?.let { error ->
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
                text = if (isSignIn) {
                    stringResource(Res.string.settings_auth_sign_in_title)
                } else {
                    stringResource(Res.string.settings_auth_sign_up_submit)
                },
                onClick = onSubmit,
                enabled = !uiState.isAuthLoading
            )

            Spacer(modifier = Modifier.height(if (isSignIn) 14.dp else 12.dp))

            if (isSignIn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.settings_auth_login_footer),
                        style = BloomTheme.typography.bodySmall,
                        color = BloomTheme.colors.textColor.secondary
                    )
                    Text(
                        text = stringResource(Res.string.settings_create_account_short),
                        modifier = Modifier.clickable(
                            enabled = !uiState.isAuthLoading,
                            onClick = onSwitchToSignUp
                        ),
                        style = BloomTheme.typography.labelLarge,
                        color = BloomTheme.colors.primary
                    )
                }
            } else {
                val termsText = buildAnnotatedString {
                    append(stringResource(Res.string.settings_auth_terms_before))
                    withStyle(
                        SpanStyle(
                            color = BloomTheme.colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(stringResource(Res.string.settings_auth_terms_rules))
                    }
                    append(stringResource(Res.string.settings_auth_terms_and))
                    withStyle(
                        SpanStyle(
                            color = BloomTheme.colors.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(stringResource(Res.string.settings_auth_terms_privacy))
                    }
                    append(stringResource(Res.string.settings_auth_terms_after_rules))
                }

                Text(
                    text = termsText,
                    modifier = Modifier.fillMaxWidth(),
                    style = BloomTheme.typography.labelSmall,
                    color = BloomTheme.colors.textColor.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (uiState.showEmailVerificationSheet) {
            EmailVerificationBottomSheet(
                onDismiss = onDismissEmailVerificationSheet
            )
        }

        if (uiState.isAuthLoading) {
            BloomLoadingAnimation(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EmailVerificationBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BloomTheme.colors.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(Res.string.settings_auth_verify_email_title),
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.textColor.primary
            )
            Text(
                text = stringResource(Res.string.settings_auth_verify_email_message),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary
            )
            BloomPrimaryFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                text = stringResource(Res.string.settings_auth_verify_email_action),
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun AuthFlowHeroIcon(isSignIn: Boolean) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BloomTheme.colors.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSignIn) {
                Icons.AutoMirrored.Filled.KeyboardArrowRight
            } else {
                Icons.Outlined.PersonAdd
            },
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = BloomTheme.colors.primary
        )
    }
}

@Composable
private fun AuthFlowTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    isError: Boolean,
    keyboardOptions: KeyboardOptions,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChanged: (Boolean) -> Unit = {}
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
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(13.dp),
        trailingIcon = if (isPassword) {
            {
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
        } else {
            null
        }
    )
}
