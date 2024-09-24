package com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.inputText.BloomSearchTextField
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import com.horizondev.habitbloom.habits.domain.models.HabitInfo
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreenModel
import com.horizondev.habitbloom.habits.presentation.components.HabitListItem
import com.horizondev.habitbloom.utils.collectAsEffect
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.choose_habit_to_acquire
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.quick_action
import habitbloom.composeapp.generated.resources.search_habit
import habitbloom.composeapp.generated.resources.select_days_for_habit
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class AddHabitDurationChoiceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()

        val screenModel = getScreenModel<AddHabitDurationChoiceScreenModel>()
        val uiState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            hostModel.updatedFlowPage(AddHabitFlowScreen.CHOOSE_DURATION)
        }

        AddHabitDurationChoiceScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
}

@Composable
fun AddHabitDurationChoiceScreenContent(
    uiState: AddHabitDurationChoiceUiState,
    handleUiEvent: (AddHabitDurationChoiceUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.choose_habit_days_and_duration),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SelectDaysForHabitCard(
            modifier = Modifier.fillMaxWidth(),
            activeDays = uiState.activeDays,
            dayStateChanged = { day, isActive ->
                handleUiEvent(AddHabitDurationChoiceUiEvent.UpdateDayState(day, isActive))
            },
            selectGroupOfDays = {
                handleUiEvent(AddHabitDurationChoiceUiEvent.SelectGroupOfDays(it))
            }
        )
    }
}

@Composable
private fun SelectDaysForHabitCard(
    modifier: Modifier = Modifier,
    activeDays: List<DayOfWeek>,
    dayStateChanged: (DayOfWeek, Boolean) -> Unit,
    selectGroupOfDays: (GroupOfDays) -> Unit
) {
    Column(
        modifier = modifier.background(
            color = BloomTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        ).padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(Res.string.select_days_for_habit),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        DayPicker(
            modifier = Modifier.fillMaxWidth(),
            activeDays = activeDays,
            dayStateChanged = dayStateChanged
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.quick_action),
            color = BloomTheme.colors.textColor.secondary,
            style = BloomTheme.typography.subheading
        )
        Spacer(modifier = Modifier.height(12.dp))
        BloomSmallActionButton(
            text = stringResource(Res.string.every_day),
            onClick = {
                selectGroupOfDays(GroupOfDays.EVERY_DAY)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        BloomSmallActionButton(
            text = stringResource(Res.string.only_weekends),
            onClick = {
                selectGroupOfDays(GroupOfDays.WEEKENDS)
            }
        )
    }
}

