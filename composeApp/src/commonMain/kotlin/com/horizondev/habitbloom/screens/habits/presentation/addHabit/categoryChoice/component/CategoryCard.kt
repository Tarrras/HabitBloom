package com.horizondev.habitbloom.screens.habits.presentation.addHabit.categoryChoice.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.HabitCategoryData
import com.horizondev.habitbloom.utils.clippedShadow
import com.horizondev.habitbloom.utils.parseHexColor

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: HabitCategoryData,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clippedShadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(BloomTheme.colors.glassBackgroundStrong)
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon container with gradient
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                category.backgroundColorHexFirst.parseHexColor().copy(alpha = 0.2f),
                                category.backgroundColorHexSecond.parseHexColor().copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = rememberAsyncImagePainter(category.iconUrl),
                    contentDescription = null,
                    tint = BloomTheme.colors.mutedForeground,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.foreground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = category.description,
                    style = BloomTheme.typography.bodyMedium,
                    color = BloomTheme.colors.mutedForeground,
                )
            }
        }
    }
}