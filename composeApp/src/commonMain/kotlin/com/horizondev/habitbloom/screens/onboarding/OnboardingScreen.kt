package com.horizondev.habitbloom.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryOutlinedButton
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.get_started
import habitbloom.composeapp.generated.resources.next
import habitbloom.composeapp.generated.resources.skip
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    navigateToMainScreen: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.uiIntents.collect { intent ->
            when (intent) {
                OnboardingUiIntent.NavigateToMainScreen -> {
                    navigateToMainScreen()
                }
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { OnboardingPage.entries.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BloomTheme.colors.background)
            .padding(16.dp)
    ) {
        // Skip button
        AnimatedVisibility(
            visible = pagerState.currentPage < OnboardingPage.entries.size - 1,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            BloomPrimaryOutlinedButton(
                text = stringResource(Res.string.skip),
                onClick = {
                    viewModel.handleUiEvent(OnboardingUiEvent.FinishOnboarding)
                },
                modifier = Modifier.padding(8.dp)
            )
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val onboardingPage = OnboardingPage.entries[page]

            OnboardingPageContent(
                page = onboardingPage,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom section with indicators and buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(OnboardingPage.entries.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) BloomTheme.colors.primary
                                else BloomTheme.colors.disabled
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next or Get Started button
            if (pagerState.currentPage == OnboardingPage.entries.size - 1) {
                BloomPrimaryFilledButton(
                    text = stringResource(Res.string.get_started),
                    onClick = {
                        viewModel.handleUiEvent(OnboardingUiEvent.FinishOnboarding)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            } else {
                BloomPrimaryFilledButton(
                    text = stringResource(Res.string.next),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@ExperimentalResourceApi
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Image(
            painter = painterResource(page.imageResource),
            contentDescription = stringResource(page.titleResource),
            modifier = Modifier
                .size(280.dp)
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = stringResource(page.titleResource),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = stringResource(page.descriptionResource),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
} 