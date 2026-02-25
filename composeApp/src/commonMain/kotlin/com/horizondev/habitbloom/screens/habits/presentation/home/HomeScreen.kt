package com.horizondev.habitbloom.screens.habits.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.calendar.DateSelectorStrip
import com.horizondev.habitbloom.core.designComponents.switcher.TimeOfDaySwitcher
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.presentation.home.components.EmptyHabitsForTimeOfDayPlaceholder
import com.horizondev.habitbloom.screens.habits.presentation.home.components.ProgressSection
import com.horizondev.habitbloom.screens.habits.presentation.home.components.UserHabitCard
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.formatToMmDdYyWithLocale
import com.horizondev.habitbloom.utils.getBackgroundGradientColors
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getTimeOfDay
import com.horizondev.habitbloom.utils.getTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_new_habit
import habitbloom.composeapp.generated.resources.afternoon_habits
import habitbloom.composeapp.generated.resources.evening_habits
import habitbloom.composeapp.generated.resources.good_afternoon
import habitbloom.composeapp.generated.resources.good_evening
import habitbloom.composeapp.generated.resources.good_morning
import habitbloom.composeapp.generated.resources.morning_habits
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * Home screen composable that displays user habits for the current time of day.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToHabitDetails: (Long) -> Unit,
    navigateToAddHabit: () -> Unit,
) {
    val uiState by viewModel.state.collectAsState()

    // Collect UI intents for navigation
    viewModel.uiIntents.collectAsEffect { uiIntent ->
        when (uiIntent) {
            is HomeScreenUiIntent.OpenHabitDetails -> {
                navigateToHabitDetails(uiIntent.userHabitId)
            }

            HomeScreenUiIntent.OpenAddNewHabit -> {
                navigateToAddHabit()
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        handleUiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeScreenUiState,
    handleUiEvent: (HomeScreenUiEvent) -> Unit
) {
    val currentTimeOfDay = remember { getTimeOfDay() }
    val backgroundColors = uiState.selectedTimeOfDay.getBackgroundGradientColors()
    val backgroundBrush = remember(backgroundColors) {
        Brush.verticalGradient(colors = backgroundColors)
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .statusBarsPadding()
        ) {
            greeting(currentTimeOfDay)
            dateSelector(
                uiState = uiState,
                onDateSelected = { handleUiEvent(HomeScreenUiEvent.SelectDate(it)) })
            timeOfDaySwitcher(
                uiState = uiState,
                onTimeOfDayChanged = {
                    handleUiEvent(HomeScreenUiEvent.SelectTimeOfDay(it))
                }
            )
            dailySummary(uiState = uiState)
            if (uiState.isLoading.not()) {
                habitsList(
                    uiState = uiState,
                    onHabitStatusChanged = { id, isCompleted ->
                        handleUiEvent(
                            HomeScreenUiEvent.ChangeHabitCompletionStatus(
                                id,
                                isCompleted
                            )
                        )
                    },
                    onHabitClicked = {
                        handleUiEvent(HomeScreenUiEvent.OpenHabitDetails(it))
                    },
                    onAddNewHabit = { handleUiEvent(HomeScreenUiEvent.AddNewHabit) }
                )
            }

            item("bottom_padding") { Spacer(modifier = Modifier.height(24.dp)) }
        }

        if (uiState.isLoading) {
            BloomLoadingAnimation(
                modifier = Modifier.align(Alignment.Center).size(150.dp),
            )
        }
    }
}

private fun LazyListScope.greeting(timeOfDay: TimeOfDay) {
    item(key = "greeting") {
        val greetingRes = when (timeOfDay) {
            TimeOfDay.Morning -> Res.string.good_morning
            TimeOfDay.Afternoon -> Res.string.good_afternoon
            TimeOfDay.Evening -> Res.string.good_evening
        }

        val today = getCurrentDate()
        val dayOfWeekName = today.dayOfWeek.getTitle()
        val formattedDate = today.formatToMmDdYyWithLocale()

        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(greetingRes),
                style = BloomTheme.typography.displaySmall,
                color = BloomTheme.colors.textColor.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$dayOfWeekName, $formattedDate",
                style = BloomTheme.typography.bodyLarge,
                color = BloomTheme.colors.textColor.secondary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun LazyListScope.dateSelector(
    uiState: HomeScreenUiState,
    onDateSelected: (LocalDate) -> Unit
) {
    item(key = "date_selector") {
        Spacer(modifier = Modifier.height(12.dp))
        DateSelectorStrip(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            selectedDate = uiState.selectedDate,
            onDateSelected = onDateSelected,
            daysToShow = 7,
            startFromToday = false
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun LazyListScope.dailySummary(
    uiState: HomeScreenUiState
) {
    item(key = "daily_summary") {
        if (uiState.totalHabitsCountForTimeOfDay > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            ProgressSection(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                selectedPeriod = uiState.selectedTimeOfDay,
                completedHabits = uiState.completedHabitsCountForTimeOfDay,
                totalHabits = uiState.totalHabitsCountForTimeOfDay
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun LazyListScope.timeOfDaySwitcher(
    uiState: HomeScreenUiState,
    onTimeOfDayChanged: (TimeOfDay) -> Unit
) {
    item(key = "time_of_day_switcher") {
        Spacer(modifier = Modifier.height(16.dp))
        TimeOfDaySwitcher(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            selectedTimeOfDay = uiState.selectedTimeOfDay,
            onTimeOfDaySelected = onTimeOfDayChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun LazyListScope.habitsList(
    uiState: HomeScreenUiState,
    onHabitStatusChanged: (Long, Boolean) -> Unit,
    onHabitClicked: (Long) -> Unit,
    onAddNewHabit: () -> Unit
) {
    // Section header
    item(key = "section_header") {
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = stringResource(
                when (uiState.selectedTimeOfDay) {
                    TimeOfDay.Morning -> Res.string.morning_habits
                    TimeOfDay.Afternoon -> Res.string.afternoon_habits
                    TimeOfDay.Evening -> Res.string.evening_habits
                }
            ),
            style = BloomTheme.typography.headlineMedium,
            color = BloomTheme.colors.textColor.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (uiState.userHabits.isEmpty()) {
        item(key = "no_habits_placeholder") {
            EmptyHabitsForTimeOfDayPlaceholder(
                selectTimeOfDay = uiState.selectedTimeOfDay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    } else {

        items(uiState.userHabits, key = { it.id }) {
            UserHabitCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = 16.dp),
                habitInfo = it,
                onCompletionStatusChanged = onHabitStatusChanged,
                editModeEnabled = uiState.habitStatusEditMode,
                onClick = { onHabitClicked(it.userHabitId) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    item("add_habit_button") {
        Spacer(modifier = Modifier.height(12.dp))
        BloomPrimaryFilledButton(
            onClick = {
                onAddNewHabit()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(
                Res.string.add_new_habit
            )
        )
    }
}
