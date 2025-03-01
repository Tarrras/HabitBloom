package com.horizondev.habitbloom.habits.presentation.createHabit.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designComponents.pickers.SingleOptionPicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.presentation.createHabit.success.CreatePersonalHabitSuccessScreen
import com.horizondev.habitbloom.platform.ImagePickerResult
import com.horizondev.habitbloom.utils.DEFAULT_PHOTO_URL
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getTitle
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
import habitbloom.composeapp.generated.resources.tap_to_change_photo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class CreatePersonalHabitScreen(
    val timeOfDay: TimeOfDay?
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CreatePersonalHabitScreenModel> {
            parametersOf(timeOfDay)
        }
        val uiState by screenModel.state.collectAsState()

        val scope = rememberCoroutineScope()
        val snackBarState = remember { SnackbarHostState() }

        screenModel.uiIntent.collectAsEffect { uiIntent ->
            when (uiIntent) {
                CreatePersonalHabitUiIntent.NavigateBack -> navigator.pop()
                CreatePersonalHabitUiIntent.OpenSuccessScreen -> {
                    navigator.replace(CreatePersonalHabitSuccessScreen())
                }

                is CreatePersonalHabitUiIntent.ShowSnackbar -> {
                    scope.launch {
                        snackBarState.showSnackbar(uiIntent.visuals)
                    }
                }
            }
        }

        CreatePersonalHabitScreenContent(
            uiState = uiState,
            snackbarHostState = snackBarState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
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

                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
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
                                handleUiEvent(CreatePersonalHabitUiEvent.PickImage)
                            },
                        iconUrl = uiState.selectedImageUrl ?: DEFAULT_PHOTO_URL,
                        contentDescription = stringResource(Res.string.create_personal_habit)
                    )

                    when (uiState.imagePickerState) {
                        is ImagePickerResult.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = BloomTheme.colors.primary
                            )
                        }

                        else -> {
                            Text(
                                text = stringResource(Res.string.tap_to_change_photo),
                                style = BloomTheme.typography.subheading,
                                color = BloomTheme.colors.textColor.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TimeOfDaySelector(
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
private fun ColumnScope.TimeOfDaySelector(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    Text(
        text = stringResource(Res.string.habit_category),
        style = BloomTheme.typography.heading,
        color = BloomTheme.colors.textColor.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    SingleOptionPicker(
        modifier = Modifier.fillMaxWidth(),
        options = TimeOfDay.entries,
        selectedOption = selectedTimeOfDay,
        onOptionSelected = {
            onTimeOfDaySelected(it)
        }, content = { option ->
            Text(
                textAlign = TextAlign.Center,
                text = option.getTitle(),
                color = if (option == selectedTimeOfDay) BloomTheme.colors.textColor.white
                else BloomTheme.colors.textColor.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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
            Spacer(modifier = Modifier.height(12.dp))
            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
            )
        }
    }
}