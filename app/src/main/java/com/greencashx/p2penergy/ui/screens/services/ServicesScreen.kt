package com.greencashx.p2penergy.ui.screens.services

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.greencashx.p2penergy.ui.screens.dashboard.GreenCashXBottomBar
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.components.GreenCashXLogo
import com.greencashx.p2penergy.ui.theme.*
import kotlinx.coroutines.delay

// ── Data ──────────────────────────────────────────────────────────────────────

data class ServiceBanner(
    val title: String,
    val subtitle: String,
    val badge: String,
    val gradientColors: List<Color>
)

data class ServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val price: String,
    val badge: String? = null,
    val badgeColor: Color = GreenPrimary
)

data class ServiceCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

private val banners = listOf(
    ServiceBanner("Upgrade Solar & Save Big!", "Save ₹5,000 on Solar Upgrade\nHurry! Limited-time offer.", "🎯 Offer", listOf(Color(0xFF16824B), Color(0xFF0FA65E))),
    ServiceBanner("Free Solar Health Check", "Book a free consultation with\ncertified GreenCashX experts.", "🆓 Free", listOf(Color(0xFF1565C0), Color(0xFF1976D2))),
    ServiceBanner("New Installation EMI", "0% EMI on new solar panel\ninstallation up to ₹1,50,000.", "💳 EMI", listOf(Color(0xFFF0A500), Color(0xFFD18A00))),
    ServiceBanner("Maintenance Package", "Annual maintenance plan at\njust ₹2,999 — save ₹1,200.", "🔧 Deal", listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA))),
)

private val bookNowServices = listOf(
    ServiceItem("solar_install", "New Solar\nInstallation", "End-to-end solar setup with grid connection", Icons.Default.WbSunny, "From ₹45,000", "Popular", GreenPrimary),
    ServiceItem("solar_maint", "Get Solar\nMaintenance", "Panel cleaning, inverter check & health report", Icons.Default.Build, "From ₹999"),
    ServiceItem("consultation", "Installation\nConsultation", "Free site survey & ROI calculation by expert", Icons.Default.Engineering, "FREE", "Free", EnergyBlue),
    ServiceItem("upgrade", "Upgrade\nSolar Panel", "Upgrade to high-efficiency monocrystalline panels", Icons.Default.Upgrade, "From ₹28,000"),
    ServiceItem("battery", "Battery\nStorage", "Add battery backup for 24/7 power independence", Icons.Default.BatteryFull, "From ₹35,000", "New", BlockchainPurple),
    ServiceItem("smart_meter", "Smart Meter\nInstallation", "Real-time energy tracking with smart IoT meter", Icons.Default.Analytics, "From ₹2,500"),
)

private val categories = listOf(
    ServiceCategory("Solar", Icons.Default.WbSunny, Color(0xFFF0A500)),
    ServiceCategory("Wind", Icons.Default.Air, EnergyBlue),
    ServiceCategory("Battery", Icons.Default.BatteryFull, GreenPrimary),
    ServiceCategory("Smart Grid", Icons.Default.Hub, BlockchainPurple),
    ServiceCategory("Carbon", Icons.Default.Park, SuccessGreen),
    ServiceCategory("Consult", Icons.Default.SupportAgent, CashGold),
)

private val featuredServices = listOf(
    ServiceItem("net_meter", "Net Metering\nSupport", "MSEDCL net metering documentation & approval", Icons.Default.ElectricMeter, "₹1,499", "MahaVitaran"),
    ServiceItem("insurance", "Solar Insurance", "Protect your solar investment against damage", Icons.Default.Security, "₹499/yr"),
    ServiceItem("monitoring", "Remote Monitoring", "24/7 cloud monitoring with alerts & reports", Icons.Default.CloudSync, "₹299/mo"),
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(navController: NavController) {
    var currentBanner by remember { mutableIntStateOf(0) }
    var bookingService by remember { mutableStateOf<ServiceItem?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            currentBanner = (currentBanner + 1) % banners.size
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GreenCashXLogo() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = OnSurfaceLight
                ),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = OnSurfaceLight)
                    }
                }
            )
        },
        bottomBar = { GreenCashXBottomBar(navController, Screen.Services.route) },
        containerColor = SurfaceVariantDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Promo Banner Carousel ─────────────────────────────────────────
            BannerCarousel(banners = banners, currentIndex = currentBanner)

            Spacer(modifier = Modifier.height(12.dp))

            // ── Service Categories ────────────────────────────────────────────
            Surface(color = BackgroundDark) {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                        TextButton(onClick = {}) { Text("View All", color = GreenPrimary, style = MaterialTheme.typography.labelMedium) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categories) { cat ->
                            CategoryChip(cat)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Book Now Grid ─────────────────────────────────────────────────
            Surface(color = BackgroundDark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Book Now", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    val chunked = bookNowServices.chunked(2)
                    chunked.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { service ->
                                BookNowCard(
                                    service = service,
                                    modifier = Modifier.weight(1f),
                                    onClick = { bookingService = service }
                                )
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Trust Banners ─────────────────────────────────────────────────
            TrustStrip()

            Spacer(modifier = Modifier.height(8.dp))

            // ── Featured / Popular Services ───────────────────────────────────
            Surface(color = BackgroundDark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text("Popular Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                        Surface(color = GreenPrimary.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                " Trending 🔥 ",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    featuredServices.forEach { service ->
                        FeaturedServiceRow(service = service, onClick = { bookingService = service })
                        if (service != featuredServices.last()) {
                            HorizontalDivider(color = SurfaceVariantDark, thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Why GreenCashX ────────────────────────────────────────────────
            WhyGreenCashXSection()

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Booking bottom sheet
    bookingService?.let { service ->
        BookingSheet(service = service, onDismiss = { bookingService = null })
    }
}

// ── Banner Carousel ───────────────────────────────────────────────────────────
@Composable
private fun BannerCarousel(banners: List<ServiceBanner>, currentIndex: Int) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.linearGradient(banners[currentIndex].gradientColors),
                )
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                    Surface(color = Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            " ${banners[currentIndex].badge} ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        banners[currentIndex].title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        banners[currentIndex].subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Learn More", color = banners[currentIndex].gradientColors[0], fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☀️", fontSize = 56.sp)
                }
            }
        }
        // Dot indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundDark)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (i == currentIndex) 18.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i == currentIndex) GreenPrimary else OnSurfaceMuted.copy(alpha = 0.4f))
                )
            }
        }
    }
}

// ── Category Chip ─────────────────────────────────────────────────────────────
@Composable
private fun CategoryChip(cat: ServiceCategory) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {}
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(cat.color.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, cat.color.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(cat.icon, contentDescription = cat.name, tint = cat.color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(cat.name, style = MaterialTheme.typography.labelSmall, color = OnSurfaceLight, fontWeight = FontWeight.Medium)
    }
}

// ── Book Now Card ─────────────────────────────────────────────────────────────
@Composable
private fun BookNowCard(service: ServiceItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(130.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BackgroundDark),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SurfaceVariantDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Column {
                Text(
                    service.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceLight,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(service.price, style = MaterialTheme.typography.labelSmall, color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
            // Icon bottom-right
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.BottomEnd)
                    .background(SurfaceVariantDark, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(service.icon, contentDescription = service.title, tint = OnSurfaceMuted, modifier = Modifier.size(22.dp))
            }
            // Badge top-right
            service.badge?.let {
                Surface(
                    color = service.badgeColor,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(" $it ", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 1.dp))
                }
            }
        }
    }
}

// ── Featured Service Row ──────────────────────────────────────────────────────
@Composable
private fun FeaturedServiceRow(service: ServiceItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(GreenPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(service.icon, contentDescription = service.title, tint = GreenPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(service.title.replace("\n", " "), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnSurfaceLight)
            Text(service.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(service.price, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = GreenPrimary)
            service.badge?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = EnergyBlue)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceMuted, modifier = Modifier.size(18.dp))
    }
}

// ── Trust Strip ───────────────────────────────────────────────────────────────
@Composable
private fun TrustStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrustPill("✅", "MSEDCL\nApproved")
        TrustPill("🏆", "5,000+\nInstalls")
        TrustPill("⭐", "4.8/5\nRating")
        TrustPill("🔧", "2 Year\nWarranty")
    }
}

@Composable
private fun TrustPill(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

// ── Why GreenCashX ────────────────────────────────────────────────────────────
@Composable
private fun WhyGreenCashXSection() {
    Surface(color = BackgroundDark) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Why Choose GreenCashX Services?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
            Spacer(modifier = Modifier.height(12.dp))
            val benefits = listOf(
                Triple(Icons.Default.Verified, "Certified Technicians", "All engineers are MSEDCL-certified with 5+ years experience"),
                Triple(Icons.Default.Payments, "Transparent Pricing", "No hidden charges — fixed price quotes before work begins"),
                Triple(Icons.Default.Schedule, "On-Time Service", "Guaranteed service within the booked time slot"),
                Triple(Icons.Default.SupportAgent, "24/7 Support", "Dedicated support team via call, chat & WhatsApp"),
            )
            benefits.forEach { (icon, title, desc) ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).background(GreenPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = title, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OnSurfaceLight)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = OnSurfaceMuted)
                    }
                }
            }
        }
    }
}

// ── Booking Bottom Sheet ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingSheet(service: ServiceItem, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var booked by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BackgroundDark) {
        if (booked) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✅", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Booking Confirmed!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Text("Our team will contact you within 2 hours to schedule your visit.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), shape = RoundedCornerShape(12.dp)) {
                    Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Book: ${service.title.replace("\n", " ")}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
                Text(service.price, style = MaterialTheme.typography.bodyMedium, color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = SurfaceVariantDark,
                    focusedLabelColor = GreenPrimary,
                    cursorColor = GreenPrimary,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark
                )
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Default.Person, null, tint = GreenPrimary) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, leadingIcon = { Icon(Icons.Default.Phone, null, tint = GreenPrimary) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Service Address") }, leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = GreenPrimary) }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 3, shape = RoundedCornerShape(12.dp), colors = fieldColors)
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) booked = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Booking", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
