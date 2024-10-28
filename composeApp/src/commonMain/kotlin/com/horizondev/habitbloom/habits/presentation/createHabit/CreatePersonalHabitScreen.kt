package com.horizondev.habitbloom.habits.presentation.createHabit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.inputText.BloomTextField
import com.horizondev.habitbloom.core.designComponents.pickers.SingleOptionPicker
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.HABIT_DESCRIPTION_MAX_LENGTH
import com.horizondev.habitbloom.utils.HABIT_TITLE_MAX_LENGTH
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.choose_habit_category
import habitbloom.composeapp.generated.resources.create_personal_habit
import habitbloom.composeapp.generated.resources.enter_habit_description
import habitbloom.composeapp.generated.resources.enter_habit_title
import habitbloom.composeapp.generated.resources.habit_description
import habitbloom.composeapp.generated.resources.habit_title
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

        screenModel.uiIntent.collectAsEffect { uiIntent ->
            when (uiIntent) {
                CreatePersonalHabitUiIntent.NavigateBack -> navigator.pop()
            }
        }

        CreatePersonalHabitScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
}

@Composable
fun CreatePersonalHabitScreenContent(
    uiState: CreatePersonalHabitUiState,
    handleUiEvent: (CreatePersonalHabitUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            BloomToolbar(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                onBackPressed = { handleUiEvent(CreatePersonalHabitUiEvent.NavigateBack) },
                title = stringResource(Res.string.create_personal_habit)
            )
        }
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

        }
    }
}

@Composable
private fun ColumnScope.TimeOfDaySelector(
    modifier: Modifier = Modifier,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit
) {
    Text(
        text = stringResource(Res.string.choose_habit_category),
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