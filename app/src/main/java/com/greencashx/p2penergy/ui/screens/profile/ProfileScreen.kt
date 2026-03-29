package com.greencashx.p2penergy.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.greencashx.p2penergy.ui.screens.dashboard.GreenCashXBottomBar
import com.greencashx.p2penergy.ui.viewmodel.AuthViewModel
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val fullName = authViewModel.getFullName()
    val userEmail = authViewModel.getUserEmail()
    val initials = authViewModel.getInitials()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var aiAlertsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark, titleContentColor = OnSurfaceLight, navigationIconContentColor = OnSurfaceLight)
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.Profile.route) },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            ProfileHeader(name = fullName, email = userEmail, initials = initials)

            Spacer(modifier = Modifier.height(16.dp))

            // Green Score
            GreenScoreCard()

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Settings Sections
                SettingsSection("Account") {
                    SettingsItem(Icons.Default.Person, "Personal Information", "KYC Verified ✓", GreenPrimary) {}
                    SettingsItem(Icons.Default.ElectricMeter, "Smart Meter Setup", "Connect your meter", EnergyBlue) {}
                    SettingsItem(Icons.Default.SolarPower, "Solar Panel Settings", "2 panels connected", CashGold) {}
                }

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSection("Preferences") {
                    SettingsToggle(Icons.Default.Notifications, "Push Notifications", notificationsEnabled) { notificationsEnabled = it }
                    SettingsToggle(Icons.Default.AutoAwesome, "AI Price Alerts", aiAlertsEnabled) { aiAlertsEnabled = it }
                    SettingsToggle(Icons.Default.Fingerprint, "Biometric Login", biometricEnabled) { biometricEnabled = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSection("Security") {
                    SettingsItem(Icons.Default.Lock, "Change Password", "", OnSurfaceMuted) {}
                    SettingsItem(Icons.Default.Shield, "Two-Factor Authentication", "Enabled", GreenPrimary) {}
                    SettingsItem(Icons.Default.Key, "Backup Wallet Phrase", "Keep it safe!", WarningAmber) {}
                }

                Spacer(modifier = Modifier.height(12.dp))

                SettingsSection("About GreenCashX") {
                    SettingsItem(Icons.Default.Info, "Version", "1.0.0", OnSurfaceMuted) {}
                    SettingsItem(Icons.Default.Description, "Terms of Service", "", OnSurfaceMuted) {}
                    SettingsItem(Icons.Default.PrivacyTip, "Privacy Policy", "", OnSurfaceMuted) {}
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logout
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Logout, null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", color = ErrorRed, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "GreenCashX v1.0.0 · Powered by AI & Blockchain\n© 2026 GreenCashX. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceMuted,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, initials: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(GreenDark.copy(alpha = 0.3f), BackgroundDark)))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(88.dp).background(GreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = BackgroundDark)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(name.ifEmpty { "Welcome" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Text(email.ifEmpty { "" }, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = GreenPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌿", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Energy Trader · Member since 2024", style = MaterialTheme.typography.labelSmall, color = GreenPrimary)
                }
            }
        }
    }
}

@Composable
private fun GreenScoreCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🏆 Green Score", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                GreenScoreStat("94/100", "Score", GreenPrimary, Modifier.weight(1f))
                GreenScoreStat("312 kg", "CO₂ Saved", GreenLight, Modifier.weight(1f))
                GreenScoreStat("47", "Credits", CashGold, Modifier.weight(1f))
                GreenScoreStat("Top 3%", "Ranking", BlockchainPurple, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GreenScoreStat(value: String, label: String, color: Color, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(title, style = MaterialTheme.typography.labelMedium, color = OnSurfaceMuted, modifier = Modifier.padding(bottom = 8.dp))
    Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(14.dp)) {
        Column { content() }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String, iconTint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)
            if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
        }
        Icon(Icons.Default.ChevronRight, null, tint = OnSurfaceMuted)
    }
    HorizontalDivider(color = SurfaceVariantDark, thickness = 0.5.dp, modifier = Modifier.padding(start = 50.dp))
}

@Composable
private fun SettingsToggle(icon: ImageVector, title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenDark))
    }
    HorizontalDivider(color = SurfaceVariantDark, thickness = 0.5.dp, modifier = Modifier.padding(start = 50.dp))
}
