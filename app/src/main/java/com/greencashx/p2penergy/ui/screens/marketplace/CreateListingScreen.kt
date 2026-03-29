package com.greencashx.p2penergy.ui.screens.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greencashx.p2penergy.data.model.EnergyType
import com.greencashx.p2penergy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    onListingCreated: () -> Unit,
    onBack: () -> Unit
) {
    var energyAmount by remember { mutableStateOf("") }
    var pricePerKwh by remember { mutableStateOf("") }
    var selectedEnergyType by remember { mutableStateOf(EnergyType.SOLAR) }
    var location by remember { mutableStateOf("") }
    var greenCertified by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    var expandedTypeMenu by remember { mutableStateOf(false) }

    val aiSuggestedPrice = 0.41 // AI-suggested optimal price

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Energy for Sale", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = OnSurfaceLight,
                    navigationIconContentColor = OnSurfaceLight
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // AI Price Suggestion Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("AI Optimal Price", style = MaterialTheme.typography.labelMedium, color = GreenPrimary)
                        Text(
                            "Based on current demand, ₹$aiSuggestedPrice/kWh will sell fastest",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceMuted
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { pricePerKwh = aiSuggestedPrice.toString() }) {
                        Text("Use", color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Energy Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Spacer(modifier = Modifier.height(12.dp))

            // Energy Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedTypeMenu,
                onExpandedChange = { expandedTypeMenu = it }
            ) {
                OutlinedTextField(
                    value = "${selectedEnergyType.icon} ${selectedEnergyType.displayName}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Energy Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SurfaceVariantDark,
                        focusedLabelColor = GreenPrimary,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedTypeMenu,
                    onDismissRequest = { expandedTypeMenu = false }
                ) {
                    EnergyType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text("${type.icon} ${type.displayName}", color = OnSurfaceLight) },
                            onClick = { selectedEnergyType = type; expandedTypeMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount & Price Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = energyAmount,
                    onValueChange = { energyAmount = it },
                    label = { Text("Amount (kWh)") },
                    leadingIcon = { Icon(Icons.Default.ElectricBolt, null, tint = EnergyBlue) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SurfaceVariantDark,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    )
                )

                OutlinedTextField(
                    value = pricePerKwh,
                    onValueChange = { pricePerKwh = it },
                    label = { Text("Price (₹/kWh)") },
                    leadingIcon = { Text("⚡", fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SurfaceVariantDark,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = GreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = SurfaceVariantDark,
                    focusedLabelColor = GreenPrimary,
                    cursorColor = GreenPrimary,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Green Certification Toggle
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌿", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Green Certified Energy", style = MaterialTheme.typography.titleSmall, color = OnSurfaceLight)
                        Text("Earn +20% premium & more carbon credits", style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
                    }
                    Switch(
                        checked = greenCertified,
                        onCheckedChange = { greenCertified = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Revenue Preview
            val amount = energyAmount.toDoubleOrNull() ?: 0.0
            val price = pricePerKwh.toDoubleOrNull() ?: 0.0
            if (amount > 0 && price > 0) {
                val revenue = amount * price
                val platformFee = revenue * 0.015 // 1.5% fee
                val netRevenue = revenue - platformFee

                Card(
                    colors = CardDefaults.cardColors(containerColor = GreenDark.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Revenue Preview", style = MaterialTheme.typography.titleSmall, color = GreenPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Gross Revenue", color = OnSurfaceMuted); Text("₹${String.format("%.2f", revenue)}", color = OnSurfaceLight)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Platform Fee (1.5%)", color = OnSurfaceMuted); Text("-₹${String.format("%.2f", platformFee)}", color = SellColor)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = SurfaceVariantDark)
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Net Revenue", color = OnSurfaceLight, fontWeight = FontWeight.Bold)
                            Text("₹${String.format("%.2f", netRevenue)}", color = GreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    isSubmitting = true
                    showConfirmation = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(16.dp),
                enabled = energyAmount.isNotBlank() && pricePerKwh.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, null, tint = BackgroundDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("List Energy on Blockchain", fontWeight = FontWeight.Bold, color = BackgroundDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "🔗 Your listing will be published as a smart contract on the blockchain",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceMuted
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = CardDark,
            title = { Text("✅ Listing Published!", fontWeight = FontWeight.Bold, color = GreenPrimary) },
            text = {
                Column {
                    Text("Your energy listing has been published to the blockchain!", color = OnSurfaceLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🔗 TX: 0x7f3a...c9e2", style = MaterialTheme.typography.labelSmall, color = BlockchainPurpleLight)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showConfirmation = false; onListingCreated() },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("View Market", color = BackgroundDark) }
            }
        )
    }
}
