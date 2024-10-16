package com.horizondev.habitbloom.habits.presentation.habitDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.BloomLoader
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallFilledActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarDayStatusColors
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarTitle
import com.horizondev.habitbloom.core.designComponents.calendar.Day
import com.horizondev.habitbloom.core.designComponents.calendar.HabitDayState
import com.horizondev.habitbloom.core.designComponents.calendar.MonthHeader
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designComponents.containers.BloomToolbar
import com.horizondev.habitbloom.core.designComponents.image.BloomNetworkImage
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.snackbar.BloomSnackbarHost
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.collectAsEffect
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.completed_repeats
import habitbloom.composeapp.generated.resources.edit
import habitbloom.composeapp.generated.resources.habit_active_days
import habitbloom.composeapp.generated.resources.habit_details
import habitbloom.composeapp.generated.resources.habit_repeats
import habitbloom.composeapp.generated.resources.habit_schedule
import habitbloom.composeapp.generated.resources.save
import habitbloom.composeapp.generated.resources.selected_repeats
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class HabitDetailsScreen(
    val userHabitId: Long
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HabitDetailsScreenModel> {
            parametersOf(userHabitId)
        }
        val uiState by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val scope = rememberCoroutineScope()
        val snackBarState = remember { SnackbarHostState() }

        screenModel.uiIntent.collectAsEffect { uiIntent ->
            when (uiIntent) {
                HabitScreenDetailsUiIntent.NavigateBack -> navigator.pop()
                is HabitScreenDetailsUiIntent.ShowSnackbar -> {
                    scope.launch {
                        snackBarState.showSnackbar(uiIntent.visuals)
                    }
                }
            }
        }


        HabitDetailsScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent,
            snackbarHostState = snackBarState
        )
    }
}

@Composable
fun HabitDetailsScreenContent(
    uiState: HabitScreenDetailsUiState,
    handleUiEvent: (HabitScreenDetailsUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = BloomTheme.colors.background,
        topBar = {
            BloomToolbar(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                title = stringResource(Res.string.habit_details),
                onBackPressed = {
                    handleUiEvent(HabitScreenDetailsUiEvent.BackPressed)
                }
            )
        },
        snackbarHost = {
            BloomSnackbarHost(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                snackBarState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.habitInfo != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    UserHabitFullInfoCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        habitInfo = uiState.habitInfo
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    UserHabitDurationCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        isEditMode = uiState.habitDurationEditMode,
                        days = uiState.habitDays,
                        repeats = uiState.habitRepeats,
                        completedRepeats = uiState.habitInfo.completedRepeats,
                        dayStateChanged = { dayOfWeek, isActive ->
                            handleUiEvent(
                                HabitScreenDetailsUiEvent.DayStateChanged(
                                    dayOfWeek = dayOfWeek,
                                    isActive = isActive
                                )
                            )
                        },
                        onEditModeChanged = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DurationEditModeChanged)
                        },
                        onDurationChanged = {
                            handleUiEvent(HabitScreenDetailsUiEvent.DurationChanged(it))
                        },
                        onUpdateHabitDuration = {
                            handleUiEvent(HabitScreenDetailsUiEvent.UpdateHabitDuration)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    UserHabitScheduleCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        habitInfo = uiState.habitInfo
                    )

                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
            BloomLoader(
                modifier = Modifier.align(Alignment.Center),
                isLoading = uiState.isLoading
            )
        }
    }
}

@Composable
private fun UserHabitFullInfoCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo
) {
    BloomSurface(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloomNetworkImage(
                    modifier = Modifier.size(48.dp),
                    contentDescription = "logo",
                    iconUrl = habitInfo.iconUrl
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = habitInfo.name,
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = habitInfo.description,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = habitInfo.shortInfo,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary
            )
        }
    }
}

@Composable
private fun UserHabitDurationCard(
    modifier: Modifier = Modifier,
    repeats: Int,
    completedRepeats: Int,
    days: List<DayOfWeek>,
    isEditMode: Boolean,
    dayStateChanged: (DayOfWeek, Boolean) -> Unit,
    onDurationChanged: (Int) -> Unit,
    onEditModeChanged: () -> Unit,
    onUpdateHabitDuration: () -> Unit
) {
    BloomSurface(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.habit_active_days),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            DayPicker(
                modifier = Modifier.fillMaxWidth(),
                activeDays = days,
                dayStateChanged = dayStateChanged,
                enabled = isEditMode
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.habit_repeats),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(isEditMode) {
                BloomSlider(
                    value = repeats.toFloat(),
                    onValueChange = { newValue -> onDurationChanged(newValue.roundToInt()) },
                    valueRange = (completedRepeats.toFloat() + 1)..12f, // Set the range from 1 to 12
                    steps = 11, // 11 steps because we start from 1
                    enabled = isEditMode
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pluralStringResource(
                    resource = Res.plurals.selected_repeats,
                    quantity = repeats,
                    repeats
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = pluralStringResource(
                    resource = Res.plurals.completed_repeats,
                    quantity = completedRepeats,
                    completedRepeats
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                textDecoration = TextDecoration.Underline,
            )
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(isEditMode) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BloomSmallFilledActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.save),
                        onClick = {
                            onUpdateHabitDuration()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BloomSmallActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.cancel),
                        onClick = {
                            onEditModeChanged()
                        }
                    )
                }
            }

            AnimatedVisibility(!isEditMode) {
                BloomSmallActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.edit),
                    onClick = {
                        onEditModeChanged()
                    }
                )
            }
        }
    }
}


@Composable
private fun UserHabitScheduleCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo
) {
    val currentMonth = remember { YearMonth.now() }
    val currentDate = remember { getCurrentDate() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(3) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = state.firstVisibleMonth

    BloomSurface(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = stringResource(Res.string.habit_schedule),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            CalendarDayStatusColors(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            CalendarTitle(
                modifier = Modifier.fillMaxWidth(),
                currentMonth = visibleMonth.yearMonth,
                goToNext = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.plusMonths(1))
                    }
                },
                goToPrevious = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(visibleMonth.yearMonth.minusMonths(1))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalCalendar(
                state = state,
                dayContent = { calendarDay ->
                    val date = calendarDay.date
                    val existingHabitRecord = habitInfo.records.find {
                        it.date == date
                    }

                    Day(
                        day = calendarDay,
                        state = when {
                            existingHabitRecord == null -> HabitDayState.None
                            existingHabitRecord.isCompleted -> HabitDayState.Completed
                            existingHabitRecord.isCompleted.not() && date < currentDate -> {
                                HabitDayState.Missed
                            }

                            else -> HabitDayState.Future
                        },
                        selected = date == currentDate
                    )
                },
                monthHeader = { month ->
                    val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                    MonthHeader(daysOfWeek = daysOfWeek, modifier = Modifier.fillMaxWidth())
                }
            )
        }
    }
}
