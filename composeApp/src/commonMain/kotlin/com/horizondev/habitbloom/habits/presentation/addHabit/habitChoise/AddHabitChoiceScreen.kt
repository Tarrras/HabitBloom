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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.dialog.BloomAlertDialog
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designComponents.list.NoResultsPlaceholders
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreenStep
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreen
import com.horizondev.habitbloom.habits.presentation.components.HabitListItem
import com.horizondev.habitbloom.habits.presentation.createHabit.details.CreatePersonalHabitScreen
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.parentOrThrow
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class AddHabitChoiceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val parentNavigator = LocalNavigator.parentOrThrow

        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()

        val screenModel = getScreenModel<AddHabitChoiceScreenModel> {
            parametersOf(hostModel.getNewHabitInfo().timeOfDay)
        }
        val uiState by screenModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            hostModel.updatedFlowPage(AddHabitFlowScreenStep.CHOOSE_HABIT)
        }

        screenModel.uiIntent.collectAsEffect { uiIntent ->
            when (uiIntent) {
                is AddHabitChoiceUiIntent.NavigateNext -> {
                    hostModel.updateSelectedHabit(uiIntent.info)
                    navigator.push(AddHabitDurationChoiceScreen())
                }

                is AddHabitChoiceUiIntent.NavigateToHabitCreation -> {
                    parentNavigator.push(CreatePersonalHabitScreen(uiIntent.timeOfDay))
                }

                is AddHabitChoiceUiIntent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(uiIntent.visuals)
                    }
                }
            }
        }

        AddHabitChoiceScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun AddHabitChoiceScreenContent(
    uiState: AddHabitChoiceUiState,
    handleUiEvent: (AddHabitChoiceUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val lazyListState = rememberLazyListState()
    val showCreateButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.choose_habit_to_acquire),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            BloomSearchTextField(
                value = uiState.searchInput,
                onValueChange = { handleUiEvent(AddHabitChoiceUiEvent.PerformSearch(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholderText = stringResource(Res.string.search_habit)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (uiState.habits.isNotEmpty()) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(top = 12.dp, bottom = 54.dp)
                ) {
                    habits(
                        habits = uiState.habits,
                        onHabitClicked = {
                            handleUiEvent(AddHabitChoiceUiEvent.SubmitHabit(it))
                        },
                        onHabitDelete = {
                            handleUiEvent(AddHabitChoiceUiEvent.DeleteHabit(it))
                        }
                    )
                }
            } else if (!uiState.isLoading) {
                NoResultsPlaceholders(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(Res.string.no_habits_found),
                    description = stringResource(Res.string.add_your_own_personal_habit_to_start_tracking),
                    buttonText = stringResource(Res.string.create_personal_habit),
                    onButtonClick = {
                        handleUiEvent(AddHabitChoiceUiEvent.CreatePersonalHabit)
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showCreateButton,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        ) {
            Column {
                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    text = stringResource(Res.string.create_personal_habit),
                    onClick = {
                        handleUiEvent(AddHabitChoiceUiEvent.CreatePersonalHabit)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )

        BloomSnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            snackBarState = snackbarHostState
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