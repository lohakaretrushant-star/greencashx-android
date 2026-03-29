package com.greencashx.p2penergy.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.components.GreenCashXLogo
import com.greencashx.p2penergy.ui.theme.*
import com.greencashx.p2penergy.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { GreenCashXBottomBar(navController = navController, currentRoute = Screen.Dashboard.route) },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Header
            DashboardHeader(navController = navController)

            // GCX Balance Card
            GcxBalanceCard()

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Actions
            QuickActionsRow(navController = navController)

            Spacer(modifier = Modifier.height(8.dp))

            // Live Market Stats
            LiveMarketSection()

            Spacer(modifier = Modifier.height(8.dp))

            // AI Recommendation
            AIRecommendationBanner(navController = navController)

            Spacer(modifier = Modifier.height(8.dp))

            // Solar Output Today
            SolarOutputCard()

            Spacer(modifier = Modifier.height(8.dp))

            // Energy Price Trend
            EnergyPriceChart()

            Spacer(modifier = Modifier.height(8.dp))

            // Referral Banner
            ReferralBanner()

            Spacer(modifier = Modifier.height(8.dp))

            // Recent Activity
            RecentActivitySection(navController = navController)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val initials = viewModel.getInitials()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            GreenCashXLogo()
            Spacer(modifier = Modifier.height(4.dp))
            Text("${viewModel.greeting},", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
            Text(if (fullName.isNotEmpty()) fullName else "Welcome! ⚡", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = OnSurfaceLight)
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(GreenPrimary, CircleShape)
                    .clickable { navController.navigate(Screen.Profile.route) },
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun GcxBalanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(GreenDark, GreenPrimary.copy(alpha = 0.8f), EnergyBlue.copy(alpha = 0.6f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INR Balance", style = MaterialTheme.typography.labelLarge, color = BackgroundDark.copy(alpha = 0.8f))
                    Surface(
                        color = BackgroundDark.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            " 🔗 Blockchain Verified ",
                            style = MaterialTheme.typography.labelSmall,
                            color = CashGold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "₹1,24,785.00",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = BackgroundDark
                )

                Text(
                    "Available for Trading",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BackgroundDark.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BalancePill(label = "Energy", value = "45.2 kWh", emoji = "⚡")
                    BalancePill(label = "CO₂ Saved", value = "312 kg", emoji = "🌱")
                    BalancePill(label = "Green Score", value = "94/100", emoji = "🏆")
                }
            }
        }
    }
}

@Composable
private fun BalancePill(label: String, value: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = BackgroundDark)
        Text(label, style = MaterialTheme.typography.labelSmall, color = BackgroundDark.copy(alpha = 0.7f))
    }
}

@Composable
private fun QuickActionsRow(navController: NavController) {
    val actions = listOf(
        QuickAction("Buy Energy", Icons.Default.ShoppingCart, EnergyBlue, Screen.MarketPlace.route),
        QuickAction("Sell Energy", Icons.Default.Sell, SuccessGreen, Screen.CreateListing.route),
        QuickAction("Services", Icons.Default.Build, GreenPrimary, Screen.Services.route),
        QuickAction("AI Insights", Icons.Default.AutoAwesome, BlockchainPurple, Screen.AIInsights.route),
        QuickAction("Wallet", Icons.Default.AccountBalanceWallet, CashGold, Screen.Wallet.route),
    )

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Quick Actions", style = MaterialTheme.typography.titleSmall, color = OnSurfaceMuted)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            actions.forEach { action ->
                QuickActionButton(action = action, onClick = { navController.navigate(action.route) })
            }
        }
    }
}

data class QuickAction(val label: String, val icon: ImageVector, val color: Color, val route: String)

@Composable
private fun QuickActionButton(action: QuickAction, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(action.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon, contentDescription = action.label, tint = action.color, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(action.label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
    }
}

@Composable
private fun LiveMarketSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Live Market", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Live", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MarketStatCard("Avg Price", "₹8.42", "/kWh", GreenPrimary, modifier = Modifier.weight(1f))
            MarketStatCard("Active Listings", "1,284", "offers", EnergyBlue, modifier = Modifier.weight(1f))
            MarketStatCard("Volume (24h)", "48.3 MWh", "traded", CashGold, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MarketStatCard(title: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(unit, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun AIRecommendationBanner(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { navController.navigate(Screen.AIInsights.route) },
        colors = CardDefaults.cardColors(containerColor = BlockchainPurple.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BlockchainPurple.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🤖", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("AI Recommendation", style = MaterialTheme.typography.labelMedium, color = BlockchainPurpleLight)
                Text(
                    "Peak demand expected at 6PM. Sell your solar surplus now for +18% premium!",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceLight
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BlockchainPurpleLight)
        }
    }
}

@Composable
private fun RecentActivitySection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            TextButton(onClick = { navController.navigate(Screen.Transactions.route) }) {
                Text("See All", color = GreenPrimary, style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val activities = listOf(
            ActivityItem("Sold Solar Energy", "12.5 kWh → ₹525.00", "2 mins ago", "☀️", BuyColor),
            ActivityItem("Bought Wind Energy", "8.0 kWh ← ₹336.00", "1 hr ago", "💨", SellColor),
            ActivityItem("Carbon Credit Earned", "+5 Carbon Credits", "3 hrs ago", "🌱", CashGold),
            ActivityItem("Energy Earnings", "+₹82.00", "5 hrs ago", "💰", BlockchainPurple),
        )

        activities.forEach { item ->
            ActivityRow(item = item)
            HorizontalDivider(color = SurfaceVariantDark, thickness = 0.5.dp)
        }
    }
}

data class ActivityItem(val title: String, val subtitle: String, val time: String, val emoji: String, val color: Color)

@Composable
private fun ActivityRow(item: ActivityItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(item.color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(item.emoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = OnSurfaceLight)
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
        }
        Text(item.time, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
    }
}

@Composable
private fun SolarOutputCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("☀️ Solar Output Today", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Surface(color = CashGold.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(" +12% vs yesterday ", style = MaterialTheme.typography.labelSmall, color = CashGold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                SolarStat("18.4 kWh", "Generated", GreenPrimary)
                SolarStat("6.2 kWh", "Consumed", EnergyBlue)
                SolarStat("12.2 kWh", "Available", CashGold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Progress Bar
            Text("Battery 78% charged", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(SurfaceVariantDark, RoundedCornerShape(3.dp))) {
                Box(modifier = Modifier.fillMaxWidth(0.78f).height(6.dp).background(
                    Brush.horizontalGradient(listOf(GreenDark, GreenPrimary)), RoundedCornerShape(3.dp)))
            }
        }
    }
}

@Composable
private fun SolarStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
    }
}

@Composable
private fun EnergyPriceChart() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("📈 Energy Price (₹/kWh)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Text("Last 6 hours", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val prices = listOf(7.8f, 8.1f, 8.4f, 9.0f, 8.7f, 9.2f)
            val labels = listOf("9am", "11am", "1pm", "3pm", "5pm", "7pm")
            val maxPrice = prices.max()
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(6.dp), Alignment.Bottom) {
                prices.forEachIndexed { i, price ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("₹${String.format("%.1f", price)}", style = MaterialTheme.typography.labelSmall, color = if (i == prices.size - 1) GreenPrimary else OnSurfaceMuted, fontSize = 8.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((60 * (price / maxPrice)).dp)
                                .background(
                                    if (i == prices.size - 1) GreenPrimary else GreenPrimary.copy(alpha = 0.35f),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(labels[i], style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferralBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(
                Brush.linearGradient(listOf(CashGold.copy(alpha = 0.25f), GreenPrimary.copy(alpha = 0.2f))),
                RoundedCornerShape(16.dp)
            ).padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎁", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Refer & Earn ₹200", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CashGold)
                    Text("Invite friends to GreenCashX and earn ₹200 in GCX credits for every referral.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = GreenPrimary, shape = RoundedCornerShape(10.dp)) {
                    Text("Invite", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BackgroundDark, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun GreenCashXBottomBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Dashboard.route),
        BottomNavItem("Market", Icons.Default.Storefront, Screen.MarketPlace.route),
        BottomNavItem("Services", Icons.Default.Build, Screen.Services.route),
        BottomNavItem("Wallet", Icons.Default.AccountBalanceWallet, Screen.Wallet.route),
        BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.route),
    )

    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    unselectedIconColor = OnSurfaceMuted,
                    unselectedTextColor = OnSurfaceMuted,
                    indicatorColor = GreenPrimary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
