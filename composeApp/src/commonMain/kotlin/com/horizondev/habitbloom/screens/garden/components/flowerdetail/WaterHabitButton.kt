package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_water_drop
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Button component for watering (completing) today's habit.
 *
 * @param isCompleted Whether the habit is already completed for today
 * @param isLoading Whether watering action is in progress
 * @param onClick Callback when the button is clicked
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun WaterHabitButton(
    isCompleted: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = if (isCompleted) {
        BloomTheme.colors.success
    } else {
        BloomTheme.colors.primary
    }

    val buttonText = if (isCompleted) {
        "Watered Today"
    } else {
        "💧 Water Today's Habit"
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onClick,
        enabled = !isLoading && !isCompleted,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = buttonColor.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        }

        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            if (!isCompleted) {
                Icon(
                    painter = painterResource(Res.drawable.ic_water_drop),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
            }

            Text(
                text = buttonText,
                style = BloomTheme.typography.button,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
} 