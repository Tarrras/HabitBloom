package com.horizondev.habitbloom.habits.presentation.addHabit.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.pickers.BloomSlider
import com.horizondev.habitbloom.core.designComponents.pickers.DayPicker
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.core.designComponents.pickers.SingleWeekStartOptionPicker
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.domain.models.GroupOfDays
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreenStep
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceUiEvent
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceUiIntent
import com.horizondev.habitbloom.utils.collectAsEffect
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.back
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.choose_habit_days_and_duration
import habitbloom.composeapp.generated.resources.complete
import habitbloom.composeapp.generated.resources.do_you_want_add_this_habit
import habitbloom.composeapp.generated.resources.every_day
import habitbloom.composeapp.generated.resources.four_repeats
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.one_repeat
import habitbloom.composeapp.generated.resources.only_weekends
import habitbloom.composeapp.generated.resources.quick_action
import habitbloom.composeapp.generated.resources.select_days_for_habit
import habitbloom.composeapp.generated.resources.select_repeats
import habitbloom.composeapp.generated.resources.selected_repeats
import habitbloom.composeapp.generated.resources.start_date
import habitbloom.composeapp.generated.resources.twelve_repeats
import habitbloom.composeapp.generated.resources.when_do_you_want_to_start
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class AddHabitSummaryScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()

        val screenModel = getScreenModel<AddHabitSummaryScreenModel>() {
            parametersOf(hostModel.getNewHabitInfo())
        }

        screenModel.uiIntent.collectAsEffect { uiIntent ->
            when (uiIntent) {
                AddHabitSummaryUiIntent.NavigateBack -> navigator.pop()
                AddHabitSummaryUiIntent.NavigateToSuccess -> TODO()
                is AddHabitSummaryUiIntent.ShowSnackBar -> {
                    hostModel.showSnackBar(uiIntent.visuals)
                }
            }
        }

        val uiState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            hostModel.updatedFlowPage(AddHabitFlowScreenStep.SUMMARY)
        }

        AddHabitSummaryScreenContent(
            uiState = uiState,
            handleUiEvent = screenModel::handleUiEvent
        )
    }
}

@Composable
fun AddHabitSummaryScreenContent(
    uiState: AddHabitSummaryUiState,
    handleUiEvent: (AddHabitSummaryUiEvent) -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.do_you_want_add_this_habit),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            SummaryHabitCard(uiState = uiState, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.complete),
                onClick = {
                    handleUiEvent(AddHabitSummaryUiEvent.Confirm)
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            BloomPrimaryOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.back),
                onClick = {
                    handleUiEvent(AddHabitSummaryUiEvent.BackPressed)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        BloomLoader(
            modifier = Modifier.align(Alignment.Center),
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun SummaryHabitCard(
    modifier: Modifier = Modifier,
    uiState: AddHabitSummaryUiState
) {
    val habitInfo = uiState.habitInfo
    Surface(
        modifier = modifier,
        color = BloomTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                KamelImage(
                    resource = asyncPainterResource(data = habitInfo.iconUrl),
                    modifier = Modifier.size(48.dp).clip(CircleShape),
                    contentDescription = habitInfo.name
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = habitInfo.name,
                        style = BloomTheme.typography.heading,
                        color = BloomTheme.colors.textColor.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habitInfo.description,
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            }

            DayPicker(
                modifier = Modifier.fillMaxWidth(),
                activeDays = uiState.days,
                dayStateChanged = { _, _ -> },
                enabled = false
            )

            Text(
                text = stringResource(Res.string.start_date, uiState.startDate),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                textDecoration = TextDecoration.Underline
            )

            Text(
                text = pluralStringResource(
                    resource = Res.plurals.selected_repeats,
                    quantity = uiState.duration,
                    uiState.duration
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}


