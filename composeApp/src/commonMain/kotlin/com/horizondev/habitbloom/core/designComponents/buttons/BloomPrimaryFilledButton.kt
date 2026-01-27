package com.horizondev.habitbloom.core.designComponents.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomPrimaryFilledButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    iconSpacing: Dp = 8.dp,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = BloomTheme.colors.primary,
        contentColor = BloomTheme.colors.textColor.white,
        disabledContainerColor = BloomTheme.colors.primary.copy(alpha = DisabledContainerOpacity)
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        contentPadding = contentPadding,
        border = null,
        colors = colors,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(iconSpacing))
            }
            Text(
                text = text,
                style = BloomTheme.typography.button,
                color = BloomTheme.colors.textColor.white,
                textAlign = TextAlign.Center
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(iconSpacing))
                trailingIcon()
            }
        }
    }
}

const val DisabledContainerOpacity = 0.2f