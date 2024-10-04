package com.horizondev.habitbloom.habits.presentation.addHabit.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceUiEvent
import com.horizondev.habitbloom.habits.presentation.home.components.placeholderImage
import com.horizondev.habitbloom.habits.presentation.home.components.placeholderText
import com.horizondev.habitbloom.habits.presentation.home.components.placeholderTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.finish
import habitbloom.composeapp.generated.resources.habit_added_description
import habitbloom.composeapp.generated.resources.habit_added_success
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.summary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AddHabitSuccessScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow



        AddHabitSuccessScreenContent(onFinish = {
            navigator.popUntilRoot()
        })
    }
}

@Composable
private fun AddHabitSuccessScreenContent(
    onFinish: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.habit_added_success),
                contentDescription = stringResource(Res.string.habit_added_success),
                modifier = Modifier.size(250.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = stringResource(Res.string.habit_added_success),
                style = BloomTheme.typography.title,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Res.string.habit_added_description),
                style = BloomTheme.typography.subheading,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center
            )
        }

        BloomPrimaryFilledButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.finish),
            onClick = {
                onFinish()
            },
        )
    }
}