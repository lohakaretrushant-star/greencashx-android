package com.greencashx.p2penergy.ui.screens.blockchain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun WalletScreen(navController: NavController) {
    var showSendDialog by remember { mutableStateOf(false) }
    var showReceiveDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Transactions", "Carbon Credits")

    val walletAddress = "0x7F3A9c...B2E9"
    val gcxBalance = 1247.85
    val ethBalance = 0.0842
    val carbonCredits = 47

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GreenCashXLogo() },
                actions = {
                    IconButton(onClick = { /* QR Scanner */ }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark, titleContentColor = OnSurfaceLight)
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.Wallet.route) },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GCX Wallet Card
            item {
                WalletCard(
                    gcxBalance = gcxBalance,
                    ethBalance = ethBalance,
                    walletAddress = walletAddress,
                    onSend = { showSendDialog = true },
                    onReceive = { showReceiveDialog = true }
                )
            }

            // Token Balances
            item {
                TokenBalancesSection(gcxBalance = gcxBalance, ethBalance = ethBalance, carbonCredits = carbonCredits)
            }

            // Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceDark,
                    contentColor = GreenPrimary,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = GreenPrimary
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(tab, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Overview: Staking & Stats
                    item { StakingCard() }
                    item { BlockchainStatsCard() }
                }
                1 -> {
                    // Transactions
                    val txs = sampleWalletTxs()
                    item {
                        Text("Recent Transactions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                    }
                    items(txs) { tx ->
                        WalletTxRow(tx = tx)
                    }
                }
                2 -> {
                    // Carbon Credits
                    item { CarbonCreditsSection(credits = carbonCredits) }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showSendDialog) SendTokenDialog(onDismiss = { showSendDialog = false })
    if (showReceiveDialog) ReceiveDialog(address = walletAddress, onDismiss = { showReceiveDialog = false })
}

@Composable
private fun WalletCard(gcxBalance: Double, ethBalance: Double, walletAddress: String, onSend: () -> Unit, onReceive: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(BlockchainPurple, EnergyBlue.copy(alpha = 0.8f))),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Energy Wallet", style = MaterialTheme.typography.labelLarge, color = OnSurfaceLight.copy(alpha = 0.8f))
                    Surface(color = BackgroundDark.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)) {
                        Text(" 🔗 Ethereum ", style = MaterialTheme.typography.labelSmall, color = OnSurfaceLight, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("₹${String.format("%,.2f", gcxBalance * 84)}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = CashGold)
                Text("Available Balance", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight.copy(alpha = 0.7f))

                Spacer(modifier = Modifier.height(6.dp))

                Text("${String.format("%.4f", ethBalance)} ETH", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(16.dp))

                // Address
                Surface(color = BackgroundDark.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, null, tint = OnSurfaceLight.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(walletAddress, style = MaterialTheme.typography.labelSmall, color = OnSurfaceLight, overflow = TextOverflow.Ellipsis, maxLines = 1)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ContentCopy, null, tint = OnSurfaceLight.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onSend, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send")
                    }
                    Button(onClick = onReceive, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = BackgroundDark.copy(alpha = 0.4f))) {
                        Icon(Icons.Default.CallReceived, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Receive")
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenBalancesSection(gcxBalance: Double, ethBalance: Double, carbonCredits: Int) {
    Column {
        Text("Token Portfolio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
        Spacer(modifier = Modifier.height(10.dp))
        TokenRow("INR", "Energy Balance", "₹${String.format("%,.2f", gcxBalance * 84)}", "Available", GreenPrimary, "⚡")
        TokenRow("ETH", "Ethereum", "${String.format("%.4f", ethBalance)}", "\$${String.format("%.2f", ethBalance * 3200)}", EnergyBlue, "🔷")
        TokenRow("CRB", "Carbon Credits", "$carbonCredits", "\$${String.format("%.2f", carbonCredits * 12.0)}", GreenLight, "🌿")
    }
}

@Composable
private fun TokenRow(symbol: String, name: String, balance: String, usdValue: String, color: Color, emoji: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(symbol, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Text(name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(balance, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                Text(usdValue, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
            }
        }
    }
}

@Composable
private fun StakingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CashGold.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CashGold.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💰", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Energy Earnings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CashGold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                StakingStat("Invested", "₹42,000", Modifier.weight(1f))
                StakingStat("APY", "12.5%", Modifier.weight(1f))
                StakingStat("Earnings", "₹268.80", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CashGold), shape = RoundedCornerShape(10.dp)) {
                Text("Manage Staking", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StakingStat(label: String, value: String, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = BackgroundDark.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CashGold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun BlockchainStatsCard() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("⛓ Network Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Spacer(modifier = Modifier.height(12.dp))
            NetworkStatRow("Gas Price", "23 GWEI", SuccessGreen)
            NetworkStatRow("Block Height", "#19,284,731", EnergyBlue)
            NetworkStatRow("Network", "Ethereum Mainnet", BlockchainPurple)
            NetworkStatRow("Smart Contract", "Verified ✓", GreenPrimary)
        }
    }
}

@Composable
private fun NetworkStatRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun WalletTxRow(tx: WalletTransaction) {
    val isIncoming = tx.type == WalletTxType.RECEIVE || tx.type == WalletTxType.REWARD
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val (emoji, color) = when (tx.type) {
                WalletTxType.SEND -> "📤" to SellColor
                WalletTxType.RECEIVE -> "📥" to BuyColor
                WalletTxType.TRADE -> "🔄" to EnergyBlue
                WalletTxType.STAKE -> "💰" to CashGold
                WalletTxType.REWARD -> "🏆" to GreenPrimary
            }
            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = OnSurfaceLight)
                Text(tx.hash.take(12) + "...", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${if (isIncoming) "+" else "-"}${String.format("%.2f", tx.amount)} ${tx.token}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (isIncoming) BuyColor else SellColor)
                Text(if (tx.status == TransactionStatus.CONFIRMED) "✓ Confirmed" else "Pending", style = MaterialTheme.typography.labelSmall, color = if (tx.status == TransactionStatus.CONFIRMED) SuccessGreen else WarningAmber)
            }
        }
    }
}

@Composable
private fun CarbonCreditsSection(credits: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = GreenDark.copy(alpha = 0.25f)), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌍", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("$credits Carbon Credits", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Text("≈ \$${credits * 12} USD Market Value", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                Spacer(modifier = Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), shape = RoundedCornerShape(10.dp)) {
                        Text("Trade Credits", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                        Text("Retire Credits", color = GreenPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun SendTokenDialog(onDismiss: () -> Unit) {
    var toAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("📤 Send Money", fontWeight = FontWeight.Bold, color = OnSurfaceLight) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = toAddress, onValueChange = { toAddress = it }, label = { Text("To (Phone / UPI / Address)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (₹)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary))
                Text("Platform Fee: 1.5% of transfer amount", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
        },
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text("Send", color = BackgroundDark) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = OnSurfaceMuted) } }
    )
}

@Composable
fun ReceiveDialog(address: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("📥 Receive Payment", fontWeight = FontWeight.Bold, color = OnSurfaceLight) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(160.dp).background(OnSurfaceLight, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text("📱\nQR Code\nPlaceholder", style = MaterialTheme.typography.labelMedium, color = BackgroundDark)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(address, style = MaterialTheme.typography.bodySmall, color = GreenPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Share this ID to receive INR payments", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
        },
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text("Copy Address", color = BackgroundDark) } }
    )
}

private fun sampleWalletTxs() = listOf(
    WalletTransaction("0x7f3a...c9e2", WalletTxType.RECEIVE, 441.0, "INR", "0xABC...", "0x7F3A...", TransactionStatus.CONFIRMED, Date(), null),
    WalletTransaction("0x1b2c...d4e5", WalletTxType.TRADE,   282.24, "INR", "0x7F3A...", "0x9D2B...", TransactionStatus.CONFIRMED, Date(), null),
    WalletTransaction("0x5e6f...a7b8", WalletTxType.REWARD,  68.88, "INR", "Contract", "0x7F3A...", TransactionStatus.CONFIRMED, Date(), null),
    WalletTransaction("0x3c4d...e5f6", WalletTxType.SEND,   1008.0, "INR", "0x7F3A...", "0xDEF...", TransactionStatus.CONFIRMED, Date(), null),
)
