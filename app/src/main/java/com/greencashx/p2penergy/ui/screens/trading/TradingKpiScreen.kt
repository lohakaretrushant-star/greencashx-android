package com.greencashx.p2penergy.ui.screens.trading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.greencashx.p2penergy.ui.theme.GreenPrimary
import com.greencashx.p2penergy.ui.viewmodel.TradingViewModel

private val BG        = Color(0xFFF4F6F8)
private val CARD      = Color(0xFFFFFFFF)
private val PRIMARY   = Color(0xFF1C1E21)
private val MUTED     = Color(0xFF65676B)
private val BUYG      = Color(0xFF1B8A3E)
private val SELLR     = Color(0xFFBE2222)
private val PURPLE    = Color(0xFF7C4DFF)
private val AMBER     = Color(0xFFF59E0B)
private val DIVIDCOL  = Color(0xFFE0E3E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingKpiScreen(
    navController: NavController,
    viewModel: TradingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val kpi = state.kpi

    LaunchedEffect(Unit) { viewModel.loadAll() }

    var showDepositDialog by remember { mutableStateOf(false) }
    var depositInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My KPI Dashboard", fontWeight = FontWeight.Bold, color = PRIMARY) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PRIMARY)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadKpi() }) {
                        Icon(Icons.Default.Refresh, null, tint = MUTED)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CARD)
            )
        },
        containerColor = BG
    ) { padding ->
        if (state.isLoading && kpi == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BUYG)
            }
            return@Scaffold
        }

        if (kpi == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ KPI data unavailable", fontSize = 16.sp, color = MUTED)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadAll() }) { Text("Retry") }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── User Type Header ─────────────────────────────────────────────
            item {
                UserTypeHeader(userType = kpi.userType, gridArea = kpi.gridArea)
            }

            // ── Wallet Card ───────────────────────────────────────────────────
            item {
                WalletKpiCard(
                    balance = kpi.walletInr,
                    activePrice = kpi.activePrice,
                    onDepositClick = { showDepositDialog = true }
                )
            }

            // ── Units Grid (Prosumer vs Consumer) ─────────────────────────────
            item {
                if (kpi.userType == "prosumer") {
                    ProsumerUnitsCard(kpi = kpi)
                } else {
                    ConsumerUnitsCard(kpi = kpi)
                }
            }

            // ── Buy Limit Card ────────────────────────────────────────────────
            item {
                BuyLimitCard(kpi = kpi)
            }

            // ── Trade Summary ─────────────────────────────────────────────────
            item {
                TradeSummaryCard(
                    settledTrades = kpi.settledTrades,
                    openOrders = kpi.openOrders,
                    totalSpent = kpi.totalSpent,
                    totalEarned = kpi.totalEarned
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("Add Money to Wallet", fontWeight = FontWeight.Bold, color = PRIMARY) },
            text = {
                Column {
                    Text("Enter amount to deposit (₹100 – ₹1,00,000)", fontSize = 13.sp, color = MUTED)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = depositInput,
                        onValueChange = { v -> if (v.isEmpty() || v.matches(Regex("^\\d{0,7}\$"))) depositInput = v },
                        label = { Text("Amount (₹)") },
                        prefix = { Text("₹") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BUYG,
                            focusedTextColor = PRIMARY,
                            unfocusedTextColor = PRIMARY
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        depositInput.toDoubleOrNull()?.let { viewModel.deposit(it) }
                        showDepositDialog = false
                        depositInput = ""
                    },
                    enabled = depositInput.toDoubleOrNull()?.let { it >= 100 && it <= 100000 } == true,
                    colors = ButtonDefaults.buttonColors(containerColor = BUYG)
                ) { Text("Deposit") }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) { Text("Cancel", color = MUTED) }
            },
            containerColor = CARD
        )
    }
}

@Composable
private fun UserTypeHeader(userType: String, gridArea: String) {
    val isProsumer = userType == "prosumer"
    val gradient = if (isProsumer)
        Brush.linearGradient(listOf(Color(0xFF1B8A3E), Color(0xFF0B5FCC)))
    else
        Brush.linearGradient(listOf(Color(0xFF3B5BDB), Color(0xFF7C4DFF)))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                if (isProsumer) "⚡ Prosumer Dashboard" else "🏠 Consumer Dashboard",
                fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (isProsumer) "Generate, Sell & Buy Units" else "Buy & Sell purchased Units only",
                fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Text("Grid: $gridArea", fontSize = 11.sp, color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun WalletKpiCard(balance: Double, activePrice: Double, onDepositClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CARD),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("💰 Wallet Balance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PRIMARY)
                TextButton(onClick = onDepositClick) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = BUYG)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Money", fontSize = 13.sp, color = BUYG)
                }
            }
            Text("₹${String.format("%.2f", balance)}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BUYG)
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = DIVIDCOL)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(14.dp), tint = MUTED)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Active Price: ₹${String.format("%.2f", activePrice)}/Unit", fontSize = 12.sp, color = MUTED)
            }
        }
    }
}

@Composable
private fun ProsumerUnitsCard(kpi: com.greencashx.p2penergy.data.remote.dto.KpiData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CARD),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("⚡ Prosumer KPIs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PRIMARY)
            Text("Available = Generated + Purchased − Sold", fontSize = 11.sp, color = MUTED)
            Spacer(modifier = Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                KpiStatBox("Generated", "${String.format("%.1f", kpi.units.generated)} U", Color(0xFF0B5FCC), Modifier.weight(1f))
                KpiStatBox("Purchased", "${String.format("%.1f", kpi.units.purchased)} U", AMBER, Modifier.weight(1f))
                KpiStatBox("Sold", "${String.format("%.1f", kpi.units.sold)} U", SELLR, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                KpiStatBox("Available", "${String.format("%.1f", kpi.units.available)} U", BUYG, Modifier.weight(1f))
                KpiStatBox("Value", "₹${String.format("%.0f", kpi.units.availableValue)}", PURPLE, Modifier.weight(1f))
                KpiStatBox("Earnings", "₹${String.format("%.0f", kpi.totalEarned)}", BUYG, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ConsumerUnitsCard(kpi: com.greencashx.p2penergy.data.remote.dto.KpiData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CARD),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏠 Consumer KPIs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PRIMARY)
            Text("Available = Bought − Sold", fontSize = 11.sp, color = MUTED)
            Spacer(modifier = Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                KpiStatBox("Total Bought", "${String.format("%.1f", kpi.units.purchased)} U", BUYG, Modifier.weight(1f))
                KpiStatBox("Total Sold", "${String.format("%.1f", kpi.units.sold)} U", SELLR, Modifier.weight(1f))
                KpiStatBox("Available", "${String.format("%.1f", kpi.units.available)} U", PURPLE, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BuyLimitCard(kpi: com.greencashx.p2penergy.data.remote.dto.KpiData) {
    val used = kpi.buyLimit.used
    val max  = kpi.buyLimit.max
    val pct  = if (max > 0) (used / max).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = CARD),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("📊 Buy Limit (DISCOM)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PRIMARY)
                if (pct >= 0.9f) {
                    Surface(color = SELLR.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp)) {
                        Text("Near Limit!", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SELLR,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }
            Text("Max = 1.2 × Last Bill (${String.format("%.1f", kpi.buyLimit.lastBillUnits)} Units)", fontSize = 11.sp, color = MUTED)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (pct >= 0.9f) SELLR else BUYG,
                trackColor = DIVIDCOL
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Used: ${String.format("%.1f", used)} Units", fontSize = 12.sp, color = MUTED)
                Text("Max: ${String.format("%.1f", max)} Units", fontSize = 12.sp, color = MUTED)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = BUYG)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remaining: ${String.format("%.1f", kpi.buyLimit.remaining)} Units",
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BUYG)
            }
        }
    }
}

@Composable
private fun TradeSummaryCard(
    settledTrades: Int,
    openOrders: Int,
    totalSpent: Double,
    totalEarned: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CARD),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📈 Trade Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PRIMARY)
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                KpiStatBox("Settled", "$settledTrades Trades", BUYG, Modifier.weight(1f))
                KpiStatBox("Open", "$openOrders Orders", AMBER, Modifier.weight(1f))
                KpiStatBox("Net P&L", "₹${String.format("%.0f", totalEarned - totalSpent)}", 
                    if (totalEarned >= totalSpent) BUYG else SELLR, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                KpiStatBox("Total Spent", "₹${String.format("%.0f", totalSpent)}", SELLR, Modifier.weight(1f))
                KpiStatBox("Total Earned", "₹${String.format("%.0f", totalEarned)}", BUYG, Modifier.weight(1f))
                KpiStatBox("Revenue", "₹${String.format("%.0f", (totalSpent + totalEarned) * 0.05)}", PURPLE, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = MUTED)
    }
}
