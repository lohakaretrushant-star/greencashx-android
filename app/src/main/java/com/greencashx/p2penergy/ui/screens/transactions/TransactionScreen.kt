package com.greencashx.p2penergy.ui.screens.transactions

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
fun TransactionScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Bought", "Sold", "Pending")

    val transactions = remember { sampleTransactions() }
    val filteredTxs = when (selectedFilter) {
        "Bought" -> transactions.filter { it.buyerId == "me" }
        "Sold" -> transactions.filter { it.sellerId == "me" }
        "Pending" -> transactions.filter { it.status == TransactionStatus.PENDING }
        else -> transactions
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GreenCashXLogo() },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark, titleContentColor = OnSurfaceLight)
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.Transactions.route) },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Summary Cards
            TransactionSummaryRow()

            // Filters
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = GreenPrimary
                        )
                    )
                }
            }

            // List
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredTxs) { tx ->
                    EnergyTransactionCard(tx = tx)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun TransactionSummaryRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard("Total Traded", "234.5 kWh", "⚡", EnergyBlue, Modifier.weight(1f))
        SummaryCard("INR Earned", "+₹7,761.60", "💰", CashGold, Modifier.weight(1f))
        SummaryCard("CO₂ Saved", "312 kg", "🌱", GreenPrimary, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(label: String, value: String, emoji: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun EnergyTransactionCard(tx: EnergyTransaction) {
    val isBuyer = tx.buyerId == "me"
    val directionColor = if (isBuyer) SellColor else BuyColor
    val directionLabel = if (isBuyer) "Bought" else "Sold"
    val directionEmoji = if (isBuyer) "🛒" else "💚"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(directionColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(directionEmoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("$directionLabel Energy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceLight)
                        Text(tx.energyType.icon + " " + tx.energyType.displayName, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
                    }
                }

                Surface(
                    color = when (tx.status) {
                        TransactionStatus.CONFIRMED -> SuccessGreen.copy(alpha = 0.15f)
                        TransactionStatus.PENDING -> WarningAmber.copy(alpha = 0.15f)
                        TransactionStatus.FAILED -> ErrorRed.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        when (tx.status) { TransactionStatus.CONFIRMED -> "✓ Confirmed"; TransactionStatus.PENDING -> "⏳ Pending"; TransactionStatus.FAILED -> "✗ Failed" },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (tx.status) { TransactionStatus.CONFIRMED -> SuccessGreen; TransactionStatus.PENDING -> WarningAmber; TransactionStatus.FAILED -> ErrorRed },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SurfaceVariantDark, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                TxDetail("Amount", "${tx.energyAmount} kWh")
                TxDetail("Price/kWh", "₹${tx.pricePerKwh}")
                TxDetail("Total", "₹${String.format("%.2f", tx.totalGcxAmount * 84)}")
                TxDetail("CO₂ Saved", "${tx.carbonOffset} kg")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "🔗 TX: ${tx.txHash}",
                style = MaterialTheme.typography.labelSmall,
                color = BlockchainPurpleLight
            )

            if (tx.blockNumber != null) {
                Text(
                    "Block #${tx.blockNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceMuted
                )
            }
        }
    }
}

@Composable
private fun TxDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = OnSurfaceLight)
    }
}

private fun sampleTransactions() = listOf(
    EnergyTransaction("t1", "0x7f3a...c9e2", "other", "me", 12.5, 0.42, 5.25, EnergyType.SOLAR, TransactionStatus.CONFIRMED, 19284731L, Date(), 5.62),
    EnergyTransaction("t2", "0x1b2c...d4e5", "me", "other", 8.0, 0.42, 3.36, EnergyType.WIND, TransactionStatus.CONFIRMED, 19284720L, Date(), 3.20),
    EnergyTransaction("t3", "0x5e6f...a7b8", "me", "another", 20.0, 0.45, 9.0, EnergyType.SOLAR, TransactionStatus.PENDING, null, Date(), 9.0),
    EnergyTransaction("t4", "0x3c4d...e5f6", "other2", "me", 35.0, 0.38, 13.3, EnergyType.HYDRO, TransactionStatus.CONFIRMED, 19284700L, Date(), 14.0),
    EnergyTransaction("t5", "0x9a0b...f1e2", "me", "other3", 10.0, 0.40, 4.0, EnergyType.WIND, TransactionStatus.CONFIRMED, 19284690L, Date(), 4.0),
)
