package com.horizondev.habitbloom.screens.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_new_habit
import habitbloom.composeapp.generated.resources.no_completed_habits_message
import habitbloom.composeapp.generated.resources.no_completed_habits_title
import org.jetbrains.compose.resources.stringResource

/**
 * A composable that displays when there are no completed habits.
 * Shows an informative message and provides a button to add new habits.
 *
 * @param modifier The modifier to be applied to the component.
 * @param onAddHabitClick The callback to be invoked when the "Add Habit" button is clicked.
 */
@Composable
fun NoCompletedHabitsPlaceholder(
    modifier: Modifier = Modifier,
    onAddHabitClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = stringResource(resource = Res.string.no_completed_habits_title),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(resource = Res.string.no_completed_habits_message),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        BloomPrimaryFilledButton(
            text = stringResource(resource = Res.string.add_new_habit),
            onClick = onAddHabitClick
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
