package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designComponents.list.NoResultsPlaceholders
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreenStep
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreen
import com.horizondev.habitbloom.habits.presentation.components.HabitListItem
import com.horizondev.habitbloom.habits.presentation.createHabit.CreatePersonalHabitScreen
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.parentOrThrow
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_your_own_personal_habit_to_start_tracking
import habitbloom.composeapp.generated.resources.choose_habit_to_acquire
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.no_habits_found
import habitbloom.composeapp.generated.resources.search_habit
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
            }
        }

        AddHabitChoiceScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
}

@Composable
fun AddHabitChoiceScreenContent(
    uiState: AddHabitChoiceUiState,
    handleUiEvent: (AddHabitChoiceUiEvent) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showCreateButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

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
            Spacer(modifier = Modifier.height(24.dp))
            if (uiState.habits.isNotEmpty()) {
                LazyColumn(
                    state = lazyListState
                ) {
                    habits(
                        habits = uiState.habits,
                        onHabitClicked = {
                            handleUiEvent(AddHabitChoiceUiEvent.SubmitHabit(it))
                        }
                    )
                }
            } else {
                NoResultsPlaceholders(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                handleUiEvent(AddHabitChoiceUiEvent.CreatePersonalHabit)
                            }
                        ),
                    title = {
                        Text(
                            textAlign = TextAlign.Center,
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontStyle = BloomTheme.typography.body.fontStyle
                                    )
                                ) {
                                    append(stringResource(Res.string.no_habits_found))
                                    append(" ")
                                }
                                withStyle(
                                    SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        fontStyle = BloomTheme.typography.body.fontStyle,
                                        color = BloomTheme.colors.primary
                                    )
                                ) {
                                    append(stringResource(Res.string.add_your_own_personal_habit_to_start_tracking))
                                }
                            }
                        )
                    }
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            visible = showCreateButton
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
    }
}

private fun LazyListScope.habits(
    habits: List<HabitInfo>,
    onHabitClicked: (HabitInfo) -> Unit
) {
    items(habits, key = { it.id }) {
        HabitListItem(
            modifier = Modifier.fillMaxWidth(),
            habitInfo = it,
            onClick = {
                onHabitClicked(it)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}