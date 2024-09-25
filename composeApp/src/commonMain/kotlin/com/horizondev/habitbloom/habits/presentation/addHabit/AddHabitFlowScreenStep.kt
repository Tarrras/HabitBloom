package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.compose.runtime.Composable
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.choose_category
import habitbloom.composeapp.generated.resources.choose_duration
import habitbloom.composeapp.generated.resources.choose_habit
import habitbloom.composeapp.generated.resources.summary
import org.jetbrains.compose.resources.stringResource

enum class AddHabitFlowScreenStep {
    CHOOSE_CATEGORY,
    CHOOSE_HABIT,
    CHOOSE_DURATION,
    SUMMARY
}

@Composable
fun AddHabitFlowScreenStep.getTitle() = when(this) {
    AddHabitFlowScreenStep.CHOOSE_CATEGORY -> stringResource(Res.string.choose_category)
    AddHabitFlowScreenStep.CHOOSE_HABIT -> stringResource(Res.string.choose_habit)
    AddHabitFlowScreenStep.CHOOSE_DURATION -> stringResource(Res.string.choose_duration)
    AddHabitFlowScreenStep.SUMMARY -> stringResource(Res.string.summary)
}