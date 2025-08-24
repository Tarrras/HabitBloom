package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getChartBorder
import com.horizondev.habitbloom.utils.getChartColor
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.all_habits_completed
import habitbloom.composeapp.generated.resources.time_to_relax
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllHabitsCompletedMessage(
    timeOfDay: TimeOfDay,
    modifier: Modifier = Modifier
) {
    BloomCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = timeOfDay.getChartColor()
        ),
        border = BorderStroke(width = 2.dp, color = timeOfDay.getChartBorder()),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sparkle icons
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨",
                    style = BloomTheme.typography.title.copy(fontSize = 32.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main message
            Text(
                text = stringResource(Res.string.all_habits_completed),
                style = BloomTheme.typography.heading.copy(fontSize = 20.sp),
                color = BloomTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(Res.string.time_to_relax),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}