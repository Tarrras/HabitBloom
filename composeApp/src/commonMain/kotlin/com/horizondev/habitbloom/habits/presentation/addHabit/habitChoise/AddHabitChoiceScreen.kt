package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designComponents.list.NoResultsPlaceholders
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarVisuals
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.presentation.components.HabitListItem
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_your_own_personal_habit_to_start_tracking
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_to_acquire
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.delete
import habitbloom.composeapp.generated.resources.delete_custom_habit_description
import habitbloom.composeapp.generated.resources.delete_custom_habit_question
import habitbloom.composeapp.generated.resources.no_habits_found
import habitbloom.composeapp.generated.resources.search_habit
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * The first screen in the Add Habit flow where users select a habit.
 */
@Composable
fun AddHabitChoiceScreen(
    timeOfDay: TimeOfDay?,
    showSnackbar: (BloomSnackbarVisuals) -> Unit,
    onHabitSelected: (HabitInfo) -> Unit,
    onCreateCustomHabit: (TimeOfDay) -> Unit,
    onBack: () -> Unit,
) {
    // Create ViewModel using Koin
    val viewModel = koinViewModel<AddHabitChoiceViewModel> {
        parametersOf(timeOfDay)
    }

    // Collect state and setup UI
    val uiState by viewModel.state.collectAsState()

    // Handle UI intents (navigation and messages)
    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { uiIntent ->
            when (uiIntent) {
                is AddHabitChoiceUiIntent.ShowSnackbar -> {
                    showSnackbar(uiIntent.visuals)
                }

                is AddHabitChoiceUiIntent.NavigateNext -> {
                    onHabitSelected(uiIntent.info)
                }

                is AddHabitChoiceUiIntent.NavigateToCreateCustomHabit -> {
                    onCreateCustomHabit(uiIntent.timeOfDay)
                }

                AddHabitChoiceUiIntent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    AddHabitChoiceScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent,
    )
}

@Composable
fun AddHabitChoiceScreenContent(
    uiState: AddHabitChoiceUiState,
    handleUiEvent: (AddHabitChoiceUiEvent) -> Unit,
) {
    // UI Content
    Box {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.choose_habit_to_acquire),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            BloomSearchTextField(
                value = uiState.searchInput,
                onValueChange = {
                    handleUiEvent(AddHabitChoiceUiEvent.UpdateSearchInput(it))
                },
                modifier = Modifier.fillMaxWidth(),
                placeholderText = stringResource(Res.string.search_habit)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (uiState.habits.isNotEmpty()) {
                HabitsList(
                    habits = uiState.habits,
                    onHabitClicked = {
                        handleUiEvent(AddHabitChoiceUiEvent.SelectHabit(it))
                    },
                    onHabitDelete = {
                        handleUiEvent(AddHabitChoiceUiEvent.DeleteHabit(it))
                    },
                    onCreateHabit = {
                        handleUiEvent(AddHabitChoiceUiEvent.CreateCustomHabit)
                    }
                )
            } else if (!uiState.isLoading) {
                NoResultsPlaceholders(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(Res.string.no_habits_found),
                    description = stringResource(Res.string.add_your_own_personal_habit_to_start_tracking),
                    buttonText = stringResource(Res.string.create_personal_habit),
                    onButtonClick = {
                        handleUiEvent(AddHabitChoiceUiEvent.CreateCustomHabit)
                    }
                )
            }
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )

        // Delete confirmation dialog
        DeleteCustomHabitDialog(
            showDialog = uiState.showDeleteDialog,
            habitName = uiState.habitToDelete?.name ?: "",
            onConfirm = { handleUiEvent(AddHabitChoiceUiEvent.ConfirmDeleteHabit) },
            onDismiss = { handleUiEvent(AddHabitChoiceUiEvent.CancelDeleteHabit) }
        )
    }
}

/**
 * Displays the list of habits.
 */
@Composable
private fun HabitsList(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit,
    onHabitDelete: (HabitInfo) -> Unit,
    onCreateHabit: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showCreateButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    Box {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(top = 12.dp, bottom = 54.dp)
        ) {
            habits(
                habits = habits,
                onHabitClicked = onHabitClicked,
                onHabitDelete = onHabitDelete
            )
        }

        AnimatedVisibility(
            visible = showCreateButton,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                text = stringResource(Res.string.create_personal_habit),
                onClick = {
                    onCreateHabit()
                }
            )
        }
    }
}

@Composable
private fun DeleteCustomHabitDialog(
    showDialog: Boolean,
    habitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        BloomAlertDialog(
            isShown = true,
            onDismiss = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.delete_custom_habit_question),
                    color = BloomTheme.colors.textColor.primary,
                    style = BloomTheme.typography.heading,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(Res.string.delete_custom_habit_description),
                    color = BloomTheme.colors.textColor.primary,
                    style = BloomTheme.typography.body,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.delete),
                    onClick = onConfirm,
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
}

private fun LazyListScope.habits(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit,
    onHabitDelete: (HabitInfo) -> Unit
) {
    items(habits, key = { it.id }) {
        HabitListItem(
            modifier = Modifier.fillMaxWidth(),
            habitInfo = it,
            onClick = {
                onHabitClicked(it)
            },
            onDelete = if (it.isCustomHabit) {
                { onHabitDelete(it) }
            } else null
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}