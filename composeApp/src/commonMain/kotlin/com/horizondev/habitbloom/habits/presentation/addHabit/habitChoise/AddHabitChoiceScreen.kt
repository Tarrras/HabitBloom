package com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowHostModel
import com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowScreen

class AddHabitChoiceScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val hostModel = navigator.getNavigatorScreenModel<AddHabitFlowHostModel>()

        LaunchedEffect(Unit) {
            hostModel.updatedFlowPage(AddHabitFlowScreen.CHOOSE_HABIT)
        }

        AddHabitChoiceScreenContent()
    }
}

@Composable
fun AddHabitChoiceScreenContent(modifier: Modifier = Modifier) {

}