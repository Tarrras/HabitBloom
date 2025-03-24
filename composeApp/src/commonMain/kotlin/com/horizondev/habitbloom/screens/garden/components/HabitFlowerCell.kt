package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.BloomingStage
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import org.jetbrains.compose.resources.painterResource

/**
 * A UI component that displays a habit as a flower in the garden.
 *
 * @param habitFlower The habit flower data to display
 * @param onClick Callback invoked when the cell is clicked
 */
@Composable
fun HabitFlowerCell(
    habitFlower: HabitFlower,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = BloomTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flower image based on the blooming stage
            val flowerResource = remember(habitFlower.bloomingStage) {
                habitFlower.bloomingStage.getEveningFlowerResource()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Show a larger image for later stages
                val size = when (habitFlower.bloomingStage) {
                    BloomingStage.SEED -> 60.dp
                    BloomingStage.SPROUT -> 65.dp
                    BloomingStage.PLANT -> 70.dp
                    BloomingStage.BUD -> 80.dp
                    BloomingStage.SMALL_FLOWER -> 90.dp
                    BloomingStage.LARGE_FLOWER -> 95.dp
                }

                Icon(
                    painter = painterResource(flowerResource),
                    contentDescription = habitFlower.name,
                    modifier = Modifier.size(size),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
            }

            // Habit name
            Text(
                text = habitFlower.name,
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Streak counter 
            Text(
                text = "${habitFlower.streak} day streak",
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
} 