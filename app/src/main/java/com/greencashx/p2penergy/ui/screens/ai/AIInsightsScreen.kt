package com.greencashx.p2penergy.ui.screens.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.greencashx.p2penergy.data.model.*
import com.greencashx.p2penergy.ui.screens.dashboard.GreenCashXBottomBar
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.components.GreenCashXLogo
import com.greencashx.p2penergy.ui.theme.*
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIInsightsScreen(navController: NavController) {
    val insights = remember { sampleInsights() }
    var isAiThinking by remember { mutableStateOf(false) }

    val rotationAnim = rememberInfiniteTransition(label = "ai_spin")
    val rotation by rotationAnim.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GreenCashXLogo() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = OnSurfaceLight
                )
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.AIInsights.route) },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Status Card
            item {
                AIPoweredHeader(isThinking = isAiThinking, rotation = rotation, onRefresh = { isAiThinking = !isAiThinking })
            }

            // Demand Forecast Chart Card
            item {
                DemandForecastCard()
            }

            // Price Prediction Card
            item {
                PricePredictionCard()
            }

            // AI Insights List
            item {
                Text("AI Recommendations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            }

            items(insights) { insight ->
                AIInsightCard(insight = insight)
            }

            // Carbon Impact AI
            item {
                CarbonImpactAICard()
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AIPoweredHeader(isThinking: Boolean, rotation: Float, onRefresh: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(BlockchainPurple.copy(alpha = 0.5f), EnergyBlue.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(BlockchainPurple.copy(alpha = 0.3f), CircleShape)
                            .rotate(if (isThinking) rotation else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GreenCashX AI Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                        Text("Analyzing 48hr energy market trends...", style = MaterialTheme.typography.bodySmall, color = BlockchainPurpleLight)
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, null, tint = BlockchainPurpleLight)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AIStatChip("Accuracy", "94.7%", GreenPrimary, Modifier.weight(1f))
                    AIStatChip("Predictions", "1,284", EnergyBlue, Modifier.weight(1f))
                    AIStatChip("Data Points", "2.3M", CashGold, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AIStatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = BackgroundDark.copy(alpha = 0.4f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun DemandForecastCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("⚡ Demand Forecast (24h)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Surface(color = EnergyBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(" AI Powered ", style = MaterialTheme.typography.labelSmall, color = EnergyBlue, modifier = Modifier.padding(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simplified visual bar chart
            val hours = listOf("6AM", "9AM", "12PM", "3PM", "6PM", "9PM")
            val demands = listOf(0.3f, 0.5f, 0.75f, 0.6f, 1.0f, 0.45f)
            val colors = listOf(GreenPrimary, GreenPrimary, EnergyBlue, EnergyBlue, SellColor, GreenPrimary)

            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                hours.forEachIndexed { i, hour ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height((demands[i] * 80).dp)
                                .background(
                                    Brush.verticalGradient(listOf(colors[i], colors[i].copy(alpha = 0.4f))),
                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(hour, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SellColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔥", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Peak demand at 6PM. Best time to sell your energy!", style = MaterialTheme.typography.bodySmall, color = WarningAmber)
            }
        }
    }
}

@Composable
private fun PricePredictionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("💰 Energy Price Trend (₹/kWh)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PredictionItem("Now", "0.42", "+0%", GreenPrimary, Modifier.weight(1f))
                PredictionItem("4h", "0.45", "+7.1%", GreenPrimary, Modifier.weight(1f))
                PredictionItem("12h", "0.51", "+21.4%", EnergyBlue, Modifier.weight(1f))
                PredictionItem("24h", "0.38", "-9.5%", SellColor, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("AI Confidence: 91.3% · Based on weather, grid load & historical patterns",
                style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun PredictionItem(time: String, price: String, change: String, color: Color, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)), shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(time, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            Text("$price", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Text(change, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
private fun AIInsightCard(insight: AIInsight) {
    val (bgColor, emoji) = when (insight.type) {
        InsightType.SELL_OPPORTUNITY -> Pair(GreenPrimary.copy(alpha = 0.1f), "📈")
        InsightType.BUY_OPPORTUNITY -> Pair(EnergyBlue.copy(alpha = 0.1f), "🛒")
        InsightType.PRICE_ALERT -> Pair(WarningAmber.copy(alpha = 0.1f), "🔔")
        InsightType.DEMAND_FORECAST -> Pair(BlockchainPurple.copy(alpha = 0.1f), "📊")
        InsightType.CARBON_MILESTONE -> Pair(GreenPrimary.copy(alpha = 0.1f), "🌱")
        InsightType.ANOMALY -> Pair(SellColor.copy(alpha = 0.1f), "⚠️")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(insight.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = GreenPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("${(insight.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = GreenPrimary, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(insight.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Text("💡 ${insight.recommendation}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight, fontWeight = FontWeight.Medium)
                if (insight.potentialSavings != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Potential: +₹${String.format("%.2f", insight.potentialSavings)}", style = MaterialTheme.typography.labelSmall, color = CashGold)
                }
            }
        }
    }
}

@Composable
private fun CarbonImpactAICard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GreenDark.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🌍 Your Carbon Impact", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Spacer(modifier = Modifier.height(14.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                CarbonStat("Trees Equivalent", "47 trees", "🌳", Modifier.weight(1f))
                CarbonStat("CO₂ Avoided", "312 kg", "♻️", Modifier.weight(1f))
                CarbonStat("Clean kWh", "742 kWh", "⚡", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { 0.74f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = SurfaceVariantDark
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text("74% toward your monthly carbon goal (1,000 kg CO₂)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun CarbonStat(label: String, value: String, emoji: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
    }
}

private fun sampleInsights() = listOf(
    AIInsight("i1", InsightType.SELL_OPPORTUNITY, "Sell Window Open", "Energy demand will spike 43% in your area by 6PM today.", 0.94f, "List your solar surplus now before the window closes", 12.50, Date()),
    AIInsight("i2", InsightType.BUY_OPPORTUNITY, "Off-Peak Buy Deal", "Wind energy prices are 18% below average until 2AM.", 0.87f, "Buy 20 kWh now for heating/storage at a discount — save ₹605", 7.20, Date()),
    AIInsight("i3", InsightType.PRICE_ALERT, "Energy Price Rising", "Energy rate increased 12% in last 2 hours on high demand.", 0.91f, "Consider selling your surplus energy before rates correct", null, Date()),
    AIInsight("i4", InsightType.CARBON_MILESTONE, "Green Goal Achievement", "You've avoided 300+ kg CO₂ emissions this month!", 1.0f, "Redeem 15 carbon credits worth ₹315 today", 3.75, Date()),
)

private fun Modifier.clip(shape: androidx.compose.ui.graphics.Shape) = this.then(Modifier.background(Color.Transparent, shape))
