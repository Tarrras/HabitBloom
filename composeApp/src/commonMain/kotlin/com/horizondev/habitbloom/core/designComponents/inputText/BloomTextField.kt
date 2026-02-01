package com.horizondev.habitbloom.core.designComponents.inputText

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.inputText.base.BloomBaseOutlinedTextField
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BloomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = BloomTheme.typography.formLabel,
    placeholderText: String? = null,
    title: String? = null,
    titleIcon: DrawableResource? = null,
    maxSymbols: Int? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(12.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = BloomTheme.colors.textColor.disabled,
        focusedBorderColor = BloomTheme.colors.primary,
        focusedTextColor = BloomTheme.colors.textColor.primary,
        unfocusedTextColor = BloomTheme.colors.textColor.primary,
        focusedContainerColor = BloomTheme.colors.surface,
        unfocusedContainerColor = BloomTheme.colors.surface,
        selectionColors = TextSelectionColors(
            backgroundColor = BloomTheme.colors.primary.copy(alpha = 0.4f),
            handleColor = BloomTheme.colors.primary
        ),
        errorContainerColor = BloomTheme.colors.surface,
        errorBorderColor = BloomTheme.colors.error
    )
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            titleIcon?.let {
                Icon(
                    painter = painterResource(titleIcon),
                    contentDescription = null,
                    tint = BloomTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            title?.let {
                Text(
                    text = title,
                    style = BloomTheme.typography.titleMedium,
                    color = BloomTheme.colors.textColor.primary
                )
            }
        }
        if (title != null || titleIcon != null) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        BloomBaseOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            placeholder = placeholderText?.let {
                {
                    Text(
                        text = it,
                        style = BloomTheme.typography.bodyLarge,
                        color = BloomTheme.colors.textColor.secondary
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            colors = colors,
            shape = shape,
            interactionSource = interactionSource,
            maxLines = maxLines,
            minLines = minLines,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        )

        maxSymbols?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${value.length}/$maxSymbols",
                style = BloomTheme.typography.labelMedium,
                color = BloomTheme.colors.textColor.secondary,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}