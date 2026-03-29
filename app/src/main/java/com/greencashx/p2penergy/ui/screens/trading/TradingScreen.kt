package com.greencashx.p2penergy.ui.screens.trading

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.greencashx.p2penergy.data.remote.dto.OrderDto
import com.greencashx.p2penergy.data.remote.dto.TradeDto
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.screens.dashboard.GreenCashXBottomBar
import com.greencashx.p2penergy.ui.theme.*
import com.greencashx.p2penergy.ui.viewmodel.TradingViewModel

private val BuyGreen  = Color(0xFF1B8A3E)
private val SellRed   = Color(0xFFBE2222)
private val BuyBg     = Color(0xFFE8F5EC)
private val SellBg    = Color(0xFFFAEAEA)
private val TextPrimary = Color(0xFF1C1E21)
private val TextMuted   = Color(0xFF65676B)
private val CardBg      = Color(0xFFFFFFFF)
private val PageBg      = Color(0xFFF4F6F8)
private val DividerCol  = Color(0xFFE0E3E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingScreen(
    navController: NavController,
    viewModel: TradingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) { viewModel.loadAll() }

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            viewModel.loadAll(isRefresh = true)
        }
    }
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) pullRefreshState.endRefresh()
    }

    // Handle session expiry — navigate to Login
    LaunchedEffect(Unit) {
        viewModel.sessionExpired.collect {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Error/Success snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Long)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("⚡ Exchange", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("National Electricity Exchange", fontSize = 11.sp, color = TextMuted)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAll() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextMuted)
                    }
                    IconButton(onClick = { navController.navigate(Screen.TradingKpi.route) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "KPI", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { GreenCashXBottomBar(navController, "trading") },
        containerColor = PageBg
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── KYC Gate Banner ───────────────────────────────────────────────
            if (state.kycStatus != "verified") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Warning, contentDescription = null,
                                    tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("KYC verification required to trade",
                                    fontSize = 13.sp, color = Color(0xFF5D3A00), fontWeight = FontWeight.Medium)
                            }
                            TextButton(onClick = { navController.navigate(Screen.Kyc.route) }) {
                                Text("Verify →", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            // ── Price Banner ─────────────────────────────────────────────────
            item {
                PriceBanner(price = state.activePrice, isLoading = state.isLoading)
            }

            // ── Wallet + Units Summary ────────────────────────────────────────
            item {
                val kpi = state.kpi
                if (kpi != null) {
                    WalletUnitsSummary(
                        walletBalance = kpi.walletInr,
                        unitsAvailable = kpi.units.available,
                        remainingBuyLimit = kpi.buyLimit.remaining,
                        userType = kpi.userType,
                        gridArea = kpi.gridArea
                    )
                }
            }

            // ── Order Entry ───────────────────────────────────────────────────
            item {
                OrderEntryCard(
                    activePrice = state.activePrice,
                    kpi = state.kpi,
                    isPlacing = state.isOrderPlacing,
                    onPlaceOrder = { type, units -> viewModel.placeOrder(type, units) }
                )
            }

            // ── Tab: Order Book / My Orders / Trades ──────────────────────────
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardBg,
                    contentColor = BuyGreen
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Order Book", modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("My Orders", modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("My Trades", modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 13.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Order Book
                    val ob = state.orderBook
                    if (ob != null) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Buy side
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("BUY ORDERS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = BuyGreen, modifier = Modifier.padding(bottom = 4.dp))
                                    if (ob.buyOrders.isEmpty()) {
                                        EmptyState("No buy orders")
                                    } else {
                                        ob.buyOrders.take(8).forEach { entry ->
                                            OrderBookRow(
                                                name = entry.name,
                                                units = entry.unitsRemaining,
                                                price = entry.pricePerUnit,
                                                isBuy = true
                                            )
                                        }
                                    }
                                }
                                Divider(modifier = Modifier.fillMaxHeight().width(1.dp), color = DividerCol)
                                // Sell side
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SELL ORDERS", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = SellRed, modifier = Modifier.padding(bottom = 4.dp))
                                    if (ob.sellOrders.isEmpty()) {
                                        EmptyState("No sell orders")
                                    } else {
                                        ob.sellOrders.take(8).forEach { entry ->
                                            OrderBookRow(
                                                name = entry.name,
                                                units = entry.unitsRemaining,
                                                price = entry.pricePerUnit,
                                                isBuy = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item { EmptyState("Loading order book...") }
                    }
                }
                1 -> {
                    // My Orders
                    if (state.orders.isEmpty()) {
                        item { EmptyState("No orders placed yet") }
                    } else {
                        items(state.orders) { order ->
                            MyOrderCard(order = order, onCancel = { viewModel.cancelOrder(order.id) })
                        }
                    }
                }
                2 -> {
                    // My Trades
                    if (state.trades.isEmpty()) {
                        item { EmptyState("No settled trades yet") }
                    } else {
                        items(state.trades) { trade ->
                            TradeCard(trade = trade)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        } // end Box
    }
}

// ── Price Banner ───────────────────────────────────────────────────────────────
@Composable
private fun PriceBanner(price: Double, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Active Unit Price", fontSize = 12.sp, color = TextMuted)
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BuyGreen)
                } else {
                    Text("₹${String.format("%.2f", price)}/Unit",
                        fontSize = 26.sp, fontWeight = FontWeight.Bold, color = BuyGreen)
                }
                Text("Platform regulated • STRICT MODE", fontSize = 10.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Buyer pays", fontSize = 11.sp, color = TextMuted)
                Text("+5% fee", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SellRed)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Seller gets", fontSize = 11.sp, color = TextMuted)
                Text("-5% fee", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BuyGreen)
            }
        }
    }
}

// ── Wallet + Units Summary ─────────────────────────────────────────────────────
@Composable
private fun WalletUnitsSummary(
    walletBalance: Double,
    unitsAvailable: Double,
    remainingBuyLimit: Double,
    userType: String,
    gridArea: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Your Account", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Surface(
                    color = if (userType == "prosumer") Color(0xFFE8F5EC) else Color(0xFFE8EEFF),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        if (userType == "prosumer") "⚡ Prosumer" else "🏠 Consumer",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = if (userType == "prosumer") BuyGreen else Color(0xFF3B5BDB),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                SummaryChip("💰 Wallet", "₹${String.format("%.2f", walletBalance)}", BuyGreen, Modifier.weight(1f))
                SummaryChip("⚡ Available", "${String.format("%.1f", unitsAvailable)} Units", Color(0xFF7C4DFF), Modifier.weight(1f))
                SummaryChip("📊 Buy Limit", "${String.format("%.1f", remainingBuyLimit)} Units left", Color(0xFFF59E0B), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Grid: $gridArea", fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// ── Order Entry Card ──────────────────────────────────────────────────────────
@Composable
private fun OrderEntryCard(
    activePrice: Double,
    kpi: com.greencashx.p2penergy.data.remote.dto.KpiData?,
    isPlacing: Boolean,
    onPlaceOrder: (String, Double) -> Unit
) {
    var orderType by remember { mutableStateOf("BUY") }
    var unitsInput by remember { mutableStateOf("") }
    var confirmDialogVisible by remember { mutableStateOf(false) }

    val units = unitsInput.toDoubleOrNull() ?: 0.0
    val isBuy = orderType == "BUY"
    val fee = units * activePrice * 0.05
    val total = units * activePrice
    val payable = if (isBuy) total + fee else total - fee
    val buttonColor  = if (isBuy) BuyGreen  else SellRed
    val buttonBgHint = if (isBuy) BuyBg     else SellBg

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            // BUY / SELL toggle
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OrderTypeButton(
                    text = "BUY", selected = isBuy, color = BuyGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { orderType = "BUY" }
                )
                OrderTypeButton(
                    text = "SELL", selected = !isBuy, color = SellRed,
                    modifier = Modifier.weight(1f),
                    onClick = { orderType = "SELL" }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Units input
            OutlinedTextField(
                value = unitsInput,
                onValueChange = { v ->
                    if (v.isEmpty() || v.matches(Regex("^\\d{0,6}(\\.\\d{0,3})?\$"))) unitsInput = v
                },
                label = { Text("Units to ${if (isBuy) "Buy" else "Sell"}") },
                placeholder = { Text("e.g. 50") },
                suffix = { Text("Units", fontSize = 13.sp, color = TextMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttonColor,
                    unfocusedBorderColor = DividerCol,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = buttonColor,
                    unfocusedLabelColor = TextMuted
                )
            )

            // Validation hint
            if (units > 0 && kpi != null) {
                Spacer(modifier = Modifier.height(6.dp))
                val hint = buildValidationHint(orderType, units, kpi)
                if (hint != null) {
                    Text(hint, fontSize = 12.sp, color = SellRed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SellBg, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calculation breakdown
            if (units > 0 && activePrice > 0) {
                Divider(color = DividerCol)
                Spacer(modifier = Modifier.height(8.dp))
                PriceRow("Unit Price", "₹${String.format("%.2f", activePrice)}", TextPrimary)
                PriceRow("Units", "${String.format("%.1f", units)}", TextPrimary)
                PriceRow("Amount", "₹${String.format("%.2f", total)}", TextPrimary)
                PriceRow(if (isBuy) "Platform Fee (+5%)" else "Platform Fee (-5%)",
                    "₹${String.format("%.2f", fee)}", TextMuted)
                Divider(color = DividerCol, modifier = Modifier.padding(vertical = 4.dp))
                PriceRow(
                    if (isBuy) "Total Payable" else "You Receive",
                    "₹${String.format("%.2f", payable)}",
                    buttonColor, bold = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // CTA Button
            Button(
                onClick = { confirmDialogVisible = true },
                enabled = units >= 1.0 && !isPlacing && buildValidationHint(orderType, units, kpi) == null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = DividerCol
                )
            ) {
                if (isPlacing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(
                        if (isBuy) Icons.Default.FlashOn else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isBuy) "BUY ${"${String.format("%.1f", units)}"} Units"
                        else       "SELL ${"${String.format("%.1f", units)}"} Units",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Confirmation dialog
    if (confirmDialogVisible && units >= 1.0) {
        AlertDialog(
            onDismissRequest = { confirmDialogVisible = false },
            title = { Text("Confirm ${if (isBuy) "BUY" else "SELL"} Order", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ConfirmRow("Order Type", if (isBuy) "BUY" else "SELL", buttonColor)
                    ConfirmRow("Seller / Buyer", if (isBuy) "Best available" else "Best available", TextMuted)
                    ConfirmRow("Units", "${String.format("%.1f", units)} Units", TextPrimary)
                    ConfirmRow("Price per Unit", "₹${String.format("%.2f", activePrice)}", TextPrimary)
                    ConfirmRow("Platform Fee", "₹${String.format("%.2f", fee)} (5%)", TextMuted)
                    Divider(color = DividerCol)
                    ConfirmRow(
                        if (isBuy) "Total Payable" else "You Receive",
                        "₹${String.format("%.2f", payable)}",
                        buttonColor, bold = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "✅ DISCOM + Ledger validated. Order will be matched via FIFO matching engine.",
                        fontSize = 11.sp, color = TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmDialogVisible = false
                        onPlaceOrder(orderType, units)
                        unitsInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Confirm & Execute", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDialogVisible = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardBg
        )
    }
}

@Composable
private fun OrderTypeButton(text: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    val bg = if (selected) color else Color.Transparent
    val textColor = if (selected) Color.White else color
    val borderColor = color
    Button(
        onClick = onClick,
        modifier = modifier
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = textColor)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
    }
}

// ── Validation Hint ────────────────────────────────────────────────────────────
private fun buildValidationHint(
    orderType: String,
    units: Double,
    kpi: com.greencashx.p2penergy.data.remote.dto.KpiData?
): String? {
    if (kpi == null) return null
    if (orderType == "BUY") {
        val remaining = kpi.buyLimit.remaining
        val required  = units * kpi.activePrice * 1.05
        if (units > remaining) {
            return "You can buy only ${String.format("%.1f", remaining)} more Units. Limit: ${String.format("%.1f", kpi.buyLimit.max)} Units (1.2× last bill of ${String.format("%.1f", kpi.buyLimit.lastBillUnits)}). Already bought: ${String.format("%.1f", kpi.buyLimit.used)}."
        }
        if (kpi.walletInr < required) {
            return "Insufficient balance. Need ₹${String.format("%.2f", required)} (incl. 5% fee). Available: ₹${String.format("%.2f", kpi.walletInr)}."
        }
    } else {
        val available = kpi.units.available
        if (units > available) {
            return "You can sell only ${String.format("%.1f", available)} Units. You tried ${String.format("%.1f", units)} Units."
        }
    }
    return null
}

@Composable
private fun PriceRow(label: String, value: String, color: Color, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}

@Composable
private fun ConfirmRow(label: String, value: String, color: Color, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = color)
    }
}

// ── Order Book Row ─────────────────────────────────────────────────────────────
@Composable
private fun OrderBookRow(name: String, units: Double, price: Double, isBuy: Boolean) {
    val bg = if (isBuy) BuyBg else SellBg
    val color = if (isBuy) BuyGreen else SellRed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("${String.format("%.1f", units)} Units", fontSize = 10.sp, color = TextMuted)
        }
        Text("₹${String.format("%.2f", price)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// ── My Order Card ──────────────────────────────────────────────────────────────
@Composable
private fun MyOrderCard(order: OrderDto, onCancel: () -> Unit) {
    val isBuy = order.orderType == "BUY"
    val color = if (isBuy) BuyGreen else SellRed
    val statusColor = when (order.status) {
        "OPEN" -> Color(0xFF1877F2)
        "PARTIAL" -> Color(0xFFF59E0B)
        "FILLED" -> BuyGreen
        "CANCELLED" -> TextMuted
        "REJECTED" -> SellRed
        else -> TextMuted
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Side badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isBuy) "B" else "S", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${order.orderType} ${String.format("%.1f", order.unitsRemaining)} / ${String.format("%.1f", order.units)} Units",
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Text("₹${String.format("%.2f", order.pricePerUnit)}/Unit",
                    fontSize = 13.sp, color = color)
                if (order.rejectionReason != null) {
                    Text(order.rejectionReason, fontSize = 11.sp, color = SellRed)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
                    Text(order.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                if (order.status in listOf("OPEN", "PARTIAL")) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = onCancel, contentPadding = PaddingValues(0.dp)) {
                        Text("Cancel", fontSize = 11.sp, color = SellRed)
                    }
                }
            }
        }
    }
}

// ── Trade Card ─────────────────────────────────────────────────────────────────
@Composable
private fun TradeCard(trade: TradeDto) {
    val isBuyer = trade.role == "BUYER"
    val color   = if (isBuyer) BuyGreen else SellRed
    val amount  = if (isBuyer) trade.buyerPayable else trade.sellerReceivable
    val statusColor = if (trade.status == "SETTLED") BuyGreen else Color(0xFFF59E0B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isBuyer) "↓" else "↑", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isBuyer) "Bought ${String.format("%.1f", trade.units)} Units"
                    else "Sold ${String.format("%.1f", trade.units)} Units",
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary
                )
                Text(
                    if (isBuyer) "From: ${trade.sellerName}" else "To: ${trade.buyerName}",
                    fontSize = 12.sp, color = TextMuted
                )
                Text("₹${String.format("%.2f", trade.pricePerUnit)}/Unit", fontSize = 12.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (isBuyer) "-₹${String.format("%.2f", amount)}"
                    else "+₹${String.format("%.2f", amount)}",
                    fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp)) {
                    Text(trade.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
        Text(text, fontSize = 13.sp, color = TextMuted)
    }
}
