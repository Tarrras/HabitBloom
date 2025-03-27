package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.garden.domain.iconWidth
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.current_streak
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
    modifier: Modifier = Modifier,
    hazeState: HazeState
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState, style = HazeMaterials.regular(
                    containerColor = BloomTheme.colors.surface
                )
            )
            .clickable {
                onClick()
            }
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flower image based on the blooming stage
            val flowerResource = remember(habitFlower.bloomingStage) {
                FlowerType.fromTimeOfDay(habitFlower.timeOfDay)
                    .getFlowerResource(habitFlower.bloomingStage)
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                // Show a larger image for later stages
                val flowerSize = habitFlower.bloomingStage.iconWidth()

                Image(
                    painter = painterResource(flowerResource),
                    contentDescription = habitFlower.name,
                    modifier = Modifier.width(flowerSize),
                    contentScale = ContentScale.FillWidth
                )
            }

            // Habit name
            Text(
                text = habitFlower.name,
                style = BloomTheme.typography.subheading.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Streak counter 
            Text(
                text = buildAnnotatedString {
                    append(
                        stringResource(Res.string.current_streak)
                    )
                    append(":")
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(habitFlower.streak.toString())
                    }
                },
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
} 