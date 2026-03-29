package com.greencashx.p2penergy.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MarketPlaceScreen(
    navController: NavController,
    onCreateListing: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showBuyDialog by remember { mutableStateOf<EnergyListing?>(null) }

    val filters = listOf("All", "☀️ Solar", "💨 Wind", "💧 Hydro", "🌿 Biomass")

    val listings = remember { sampleListings() }
    val filteredListings = listings.filter { listing ->
        (selectedFilter == "All" || listing.energyType.displayName in selectedFilter) &&
        (searchQuery.isEmpty() || listing.sellerName.contains(searchQuery, ignoreCase = true) ||
                listing.location.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GreenCashXLogo() },
                actions = {
                    IconButton(onClick = onCreateListing) {
                        Icon(Icons.Default.Add, contentDescription = "List Energy", tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = OnSurfaceLight
                )
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.MarketPlace.route) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateListing,
                containerColor = GreenPrimary,
                contentColor = BackgroundDark
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sell Energy", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by seller, location...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = OnSurfaceMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null, tint = OnSurfaceMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = SurfaceVariantDark,
                    cursorColor = GreenPrimary,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                )
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
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

            Spacer(modifier = Modifier.height(8.dp))

            // Results count
            Text(
                "${filteredListings.size} listings available",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceMuted,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Listings
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredListings) { listing ->
                    EnergyListingCard(
                        listing = listing,
                        onBuy = { showBuyDialog = listing }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Buy Confirmation Dialog
    showBuyDialog?.let { listing ->
        BuyEnergyDialog(
            listing = listing,
            onConfirm = { showBuyDialog = null },
            onDismiss = { showBuyDialog = null }
        )
    }
}

@Composable
fun EnergyListingCard(listing: EnergyListing, onBuy: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(GreenPrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(listing.energyType.icon, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(listing.sellerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceLight)
                        Text("📍 ${listing.location} · ${listing.distance} km away", style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
                    }
                }

                if (listing.greenCertified) {
                    Surface(
                        color = GreenPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            " 🌿 Certified ",
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenPrimary,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(label = "Amount", value = "${listing.energyAmount} kWh", color = EnergyBlue)
                InfoChip(label = "Price", value = "₹${listing.pricePerKwh}/kWh", color = CashGold)
                InfoChip(label = "CO₂ Credits", value = "+${listing.carbonCredits}", color = GreenPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val total = listing.energyAmount * listing.pricePerKwh
                Column {
                    Text("Total Cost", style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                    Text("₹${String.format("%.2f", total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CashGold)
                }

                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Buy Now", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun BuyEnergyDialog(
    listing: EnergyListing,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf(listing.energyAmount.toString()) }
    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
    val totalCost = parsedAmount * listing.pricePerKwh

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Text("Confirm Purchase", fontWeight = FontWeight.Bold, color = OnSurfaceLight)
        },
        text = {
            Column {
                Text("From: ${listing.sellerName}", color = OnSurfaceMuted, style = MaterialTheme.typography.bodySmall)
                Text("Energy Type: ${listing.energyType.icon} ${listing.energyType.displayName}", color = OnSurfaceMuted, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (kWh)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        cursorColor = GreenPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = SurfaceVariantDark)
                Spacer(modifier = Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total Cost", color = OnSurfaceMuted)
                    Text("₹${String.format("%.2f", totalCost)}", color = CashGold, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("CO₂ Credits Earned", color = OnSurfaceMuted)
                    Text("+${listing.carbonCredits}", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("🔗 Transaction will be recorded on blockchain", style = MaterialTheme.typography.labelSmall, color = BlockchainPurpleLight)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Confirm & Pay", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceMuted)
            }
        }
    )
}

private fun sampleListings() = listOf(
    EnergyListing("1", "u1", "SunFarm Co.", 25.0, 0.38, EnergyType.SOLAR, "Austin, TX", 2.3, Date(), Date(), true, 8),
    EnergyListing("2", "u2", "WindPeak LLC", 40.0, 0.35, EnergyType.WIND, "Denver, CO", 5.1, Date(), Date(), true, 12),
    EnergyListing("3", "u3", "HydroStream", 60.0, 0.32, EnergyType.HYDRO, "Portland, OR", 8.7, Date(), Date(), false, 15),
    EnergyListing("4", "u4", "GreenRoof Solar", 15.0, 0.42, EnergyType.SOLAR, "Austin, TX", 1.2, Date(), Date(), true, 5),
    EnergyListing("5", "u5", "BioEnergy Hub", 80.0, 0.30, EnergyType.BIOMASS, "Chicago, IL", 12.4, Date(), Date(), false, 20),
)
