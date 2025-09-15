package com.horizondev.habitbloom.core.designComponents.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.back
import habitbloom.composeapp.generated.resources.next
import org.jetbrains.compose.resources.stringResource

@Composable
fun BloomBottomActionButtonContainer(
    modifier: Modifier = Modifier,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: () -> Unit,
    primaryButtonEnabled: Boolean
) {
    Column(
        modifier = modifier.background(color = BloomTheme.colors.background)
    ) {
        HorizontalDivider(color = BloomTheme.colors.border)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BloomPrimaryOutlinedButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.back),
                onClick = {
                    onPrimaryButtonClicked()
                },
            )
            Spacer(modifier = Modifier.width(12.dp))
            BloomPrimaryFilledButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.next),
                onClick = {
                    onSecondaryButtonClicked()
                },
                enabled = primaryButtonEnabled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())

    }
}