package com.horizondev.habitbloom.screens.habits.presentation.createHabit.details

import CreatePersonalHabitViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.components.BloomLoader
import com.horizondev.habitbloom.core.designComponents.containers.BloomGrid
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import com.horizondev.habitbloom.utils.clippedShadow
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.back
import habitbloom.composeapp.generated.resources.choose_habit_icon
import habitbloom.composeapp.generated.resources.create
import habitbloom.composeapp.generated.resources.create_habit_description
import habitbloom.composeapp.generated.resources.create_habit_question
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.create_personal_habit_subtitle
import habitbloom.composeapp.generated.resources.enter_habit_description
import habitbloom.composeapp.generated.resources.enter_habit_title
import habitbloom.composeapp.generated.resources.habit_description
import habitbloom.composeapp.generated.resources.habit_title
import habitbloom.composeapp.generated.resources.ic_lucid_file_text
import habitbloom.composeapp.generated.resources.ic_lucid_image
import habitbloom.composeapp.generated.resources.ic_lucid_type
import habitbloom.composeapp.generated.resources.next
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Screen for creating a personal habit.
 */
@Composable
fun CreatePersonalHabitScreen(
    viewModel: CreatePersonalHabitViewModel,
    onNavigateBack: () -> Unit,
    onOpenSuccessScreen: () -> Unit,
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
            containerColor = BloomTheme.colors.background,
            snackbarHost = {
                BloomSnackbarHost(
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    snackBarState = snackbarHostState
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier.background(color = BloomTheme.colors.background)
                ) {
                    HorizontalDivider(color = BloomTheme.colors.border)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BloomPrimaryOutlinedButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(Res.string.back),
                            onClick = {
                                handleUiEvent(CreatePersonalHabitUiEvent.NavigateBack)
                            },
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        BloomPrimaryFilledButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(Res.string.next),
                            onClick = {
                                handleUiEvent(CreatePersonalHabitUiEvent.CreateHabit)
                            },
                            enabled = uiState.nextButtonEnabled
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.navigationBarsPadding())

                }
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

                Text(
                    text = stringResource(Res.string.create_personal_habit),
                    style = BloomTheme.typography.headlineLarge,
                    color = BloomTheme.colors.textColor.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.create_personal_habit_subtitle),
                    style = BloomTheme.typography.bodyLarge,
                    color = BloomTheme.colors.textColor.secondary,
                )

                Spacer(modifier = Modifier.height(32.dp))
                BloomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    title = stringResource(Res.string.habit_title),
                    titleIcon = Res.drawable.ic_lucid_type,
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
                    titleIcon = Res.drawable.ic_lucid_file_text,
                    placeholderText = stringResource(Res.string.enter_habit_description),
                    maxSymbols = HABIT_DESCRIPTION_MAX_LENGTH,
                    onValueChange = {
                        handleUiEvent(CreatePersonalHabitUiEvent.UpdateDescription(it))
                    },
                    isError = uiState.isDescriptionInputError,
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                CreateHabitIconSelector(
                    availableIcons = uiState.availableIcons,
                    selectedIconUrl = uiState.selectedImageUrl,
                    isLoadingIcons = uiState.isLoadingIcons,
                    onIconSelected = { iconUrl ->
                        handleUiEvent(CreatePersonalHabitUiEvent.SelectIcon(iconUrl))
                    }
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
private fun CreateHabitIconSelector(
    availableIcons: List<String>,
    selectedIconUrl: String,
    isLoadingIcons: Boolean,
    onIconSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_lucid_image),
            contentDescription = "Icon selector",
            tint = BloomTheme.colors.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(Res.string.choose_habit_icon),
            style = BloomTheme.typography.titleMedium,
            color = BloomTheme.colors.textColor.primary
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    if (isLoadingIcons) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        BloomGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = 4,
            verticalSpacing = 24.dp,
            horizontalSpacing = 24.dp
        ) {
            availableIcons.forEach { iconUrl ->
                IconItem(
                    iconUrl = iconUrl,
                    isSelected = iconUrl == selectedIconUrl,
                    onClick = { onIconSelected(iconUrl) }
                )
            }
        }
    }
}

@Composable
private fun IconItem(
    iconUrl: String,
    isSelected: Boolean,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clippedShadow(elevation = 2.dp, shape = shape)
            .clip(shape)
            .background(
                if (isSelected) BloomTheme.colors.primary.copy(alpha = 0.1f)
                else BloomTheme.colors.glassBackground,
                shape = shape
            )
            .then(
                if (isSelected) Modifier.border(
                    width = 1.dp,
                    color = BloomTheme.colors.primary,
                    shape = shape
                ) else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        BloomNetworkImage(
            iconUrl = iconUrl,
            modifier = Modifier.size(32.dp),
            contentDescription = "Habit icon",
            shape = RectangleShape
        )
    }
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

