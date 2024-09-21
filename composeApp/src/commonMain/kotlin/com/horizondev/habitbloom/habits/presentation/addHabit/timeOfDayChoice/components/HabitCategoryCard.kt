package com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun HabitCategoryCard(
    title: String,
    description: String,
    imageRes: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp), // Rounded corners for a soft appearance
        colors = CardDefaults.cardColors(
            containerColor = BloomTheme.colors.surface,
            contentColor = Color.Unspecified
        ),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            // Large Image
            Image(
                painter = imageRes,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Habit Title
            Text(
                text = title,
                style = BloomTheme.typography.heading, // Use the 20sp Medium style
                color = BloomTheme.colors.textColor.primary, // Dark text color
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Habit Description
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = description,
                style = BloomTheme.typography.body, // Use the 20sp Medium style
                color = BloomTheme.colors.textColor.secondary // Dark text color
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}