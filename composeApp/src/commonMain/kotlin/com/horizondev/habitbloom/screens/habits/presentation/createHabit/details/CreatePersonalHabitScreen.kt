package com.horizondev.habitbloom.screens.habits.presentation.createHabit.details

import CreatePersonalHabitViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designComponents.switcher.TimeOfDaySwitcher
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.platform.ImagePickerResult
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.create
import habitbloom.composeapp.generated.resources.create_habit_description
import habitbloom.composeapp.generated.resources.create_habit_question
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.enter_habit_description
import habitbloom.composeapp.generated.resources.enter_habit_title
import habitbloom.composeapp.generated.resources.habit_category
import habitbloom.composeapp.generated.resources.habit_description
import habitbloom.composeapp.generated.resources.habit_title
import habitbloom.composeapp.generated.resources.next
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * Screen for creating a personal habit.
 */
@Composable
fun CreatePersonalHabitScreen(
    viewModel: CreatePersonalHabitViewModel,
    onNavigateBack: () -> Unit,
    onOpenSuccessScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                CreatePersonalHabitUiIntent.NavigateBack -> onNavigateBack()
                CreatePersonalHabitUiIntent.OpenSuccessScreen -> onOpenSuccessScreen()
                is CreatePersonalHabitUiIntent.ShowSnackbar -> {
                    scope.launch {
                        snackBarState.showSnackbar(uiIntent.visuals)
                    }
                }
            }
        }
    }

    CreatePersonalHabitScreenContent(
        uiState = uiState,
        snackbarHostState = snackBarState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
fun CreatePersonalHabitScreenContent(
    uiState: CreatePersonalHabitUiState,
    snackbarHostState: SnackbarHostState,
    handleUiEvent: (CreatePersonalHabitUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                BloomToolbar(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    onBackPressed = { handleUiEvent(CreatePersonalHabitUiEvent.NavigateBack) },
                    title = stringResource(Res.string.create_personal_habit)
                )
            },
            containerColor = BloomTheme.colors.background,
            snackbarHost = {
                BloomSnackbarHost(
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    snackBarState = snackbarHostState
                )
            },
            modifier = Modifier.imePadding()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CreateHabitIconPicker(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imagePickerState = uiState.imagePickerState,
                    selectedImageUri = uiState.selectedImageUrl,
                    onPickImage = {
                        handleUiEvent(CreatePersonalHabitUiEvent.PickImage)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CreateHabitTimeOfDaySelector(
                    selectedTimeOfDay = uiState.timeOfDay,
                    onTimeOfDaySelected = {
                        handleUiEvent(CreatePersonalHabitUiEvent.UpdateTimeOfDay(it))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                BloomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    title = stringResource(Res.string.habit_title),
                    placeholderText = stringResource(Res.string.enter_habit_title),
                    maxSymbols = HABIT_TITLE_MAX_LENGTH,
                    onValueChange = {
                        handleUiEvent(CreatePersonalHabitUiEvent.UpdateTitle(it))
                    },
                    isError = uiState.isTitleInputError
                )
                Spacer(modifier = Modifier.height(16.dp))
                BloomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.description,
                    title = stringResource(Res.string.habit_description),
                    placeholderText = stringResource(Res.string.enter_habit_description),
                    maxSymbols = HABIT_DESCRIPTION_MAX_LENGTH,
                    onValueChange = {
                        handleUiEvent(CreatePersonalHabitUiEvent.UpdateDescription(it))
                    },
                    isError = uiState.isDescriptionInputError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))

                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.next),
                    onClick = {
                        handleUiEvent(CreatePersonalHabitUiEvent.CreateHabit)
                    },
                    enabled = uiState.nextButtonEnabled
                )
                Spacer(modifier = Modifier.height(12.dp))
                BloomPrimaryOutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.cancel),
                    onClick = {
                        handleUiEvent(CreatePersonalHabitUiEvent.NavigateBack)
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        CreateHabitDialog(
            showCreateDialog = uiState.showCreateHabitDialog,
            onDismiss = {
                handleUiEvent(CreatePersonalHabitUiEvent.HideCreateHabitDialog)
            }, onCreate = {
                handleUiEvent(CreatePersonalHabitUiEvent.SubmitHabitCreation)
            }
        )

        BloomLoader(isLoading = uiState.isLoading, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun CreateHabitTimeOfDaySelector(
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    Text(
        text = stringResource(Res.string.habit_category),
        style = BloomTheme.typography.heading,
        color = BloomTheme.colors.textColor.primary
    )
    Spacer(modifier = Modifier.height(8.dp))

    TimeOfDaySwitcher(
        modifier = Modifier.fillMaxWidth(),
        selectedTimeOfDay = selectedTimeOfDay, onTimeOfDaySelected = onTimeOfDaySelected
    )
}

@Composable
private fun CreateHabitDialog(
    modifier: Modifier = Modifier,
    showCreateDialog: Boolean,
    onDismiss: () -> Unit,
    onCreate: () -> Unit
) {
    BloomAlertDialog(
        isShown = showCreateDialog,
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.create_habit_question),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.heading,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(Res.string.create_habit_description),
                color = BloomTheme.colors.textColor.primary,
                style = BloomTheme.typography.body,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.create),
                onClick = onCreate,
            )
        }
    }
}

@Composable
fun CreateHabitIconPicker(
    modifier: Modifier = Modifier,
    imagePickerState: ImagePickerResult,
    selectedImageUri: String?,
    onPickImage: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        BloomNetworkImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(124.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onPickImage()
                },
            iconUrl = selectedImageUri ?: DEFAULT_PHOTO_URL,
            contentDescription = stringResource(Res.string.create_personal_habit)
        )

        when (imagePickerState) {
            is ImagePickerResult.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = BloomTheme.colors.primary
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(color = BloomTheme.colors.primary, shape = CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false),
                            onClick = {
                                onPickImage()
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        tint = BloomTheme.colors.surface,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}