package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.competed_afternoon_habits
import habitbloom.composeapp.generated.resources.competed_evening_habits
import habitbloom.composeapp.generated.resources.competed_morning_habits
import habitbloom.composeapp.generated.resources.ic_confetti
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllHabitsCompletedMessage(
    timeOfDay: TimeOfDay,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = BloomTheme.colors.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time of day-specific message
            val message = when (timeOfDay) {
                TimeOfDay.Morning -> stringResource(Res.string.competed_morning_habits)
                TimeOfDay.Afternoon -> stringResource(Res.string.competed_afternoon_habits)
                TimeOfDay.Evening -> stringResource(Res.string.competed_evening_habits)
            }

            // Optional: add a celebratory icon
            Image(
                painter = painterResource(resource = Res.drawable.ic_confetti),
                contentDescription = "Celebration",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Display message text
            Text(
                text = message,
                style = BloomTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                color = BloomTheme.colors.textColor.primary, // Dark text color
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }
    }
}