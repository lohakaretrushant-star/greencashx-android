package com.greencashx.p2penergy.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greencashx.p2penergy.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val accentColor: androidx.compose.ui.graphics.Color
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    val pages = listOf(
        OnboardingPage("☀️", "Trade Clean Energy", "Buy and sell renewable energy directly with your neighbors. No middlemen, just pure P2P energy exchange.", GreenPrimary),
        OnboardingPage("🤖", "AI-Powered Insights", "Our AI engine forecasts energy demand, suggests optimal trading times, and maximizes your earnings automatically.", EnergyBlue),
        OnboardingPage("🔗", "Blockchain Secured", "Every transaction is recorded on the blockchain. Trade energy in INR — earn while saving the planet.", BlockchainPurple),
        OnboardingPage("🌍", "Earn Carbon Credits", "Every kWh of clean energy traded earns you carbon credits. Build your green score and help fight climate change.", CashGold)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        // Indicators & Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) GreenPrimary
                                else OnSurfaceMuted.copy(alpha = 0.4f)
                            )
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (pagerState.currentPage == pages.lastIndex) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Get Started — It's Free",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onLogin) {
                    Text(
                        "Already have an account? Sign In",
                        color = OnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onGetStarted) {
                        Text("Skip", color = OnSurfaceMuted)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next →", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(
                    Brush.radialGradient(
                        listOf(page.accentColor.copy(alpha = 0.2f), BackgroundDark)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, fontSize = 72.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = page.accentColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceMuted,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}
