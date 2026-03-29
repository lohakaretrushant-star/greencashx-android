package com.greencashx.p2penergy.ui.screens.kyc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greencashx.p2penergy.navigation.Screen
import com.greencashx.p2penergy.ui.theme.*
import com.greencashx.p2penergy.ui.viewmodel.KycViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    navController: NavController,
    viewModel: KycViewModel = hiltViewModel()
) {
    var step by remember { mutableIntStateOf(0) }  // 0=intro, 1=form, 2=success
    var documentType by remember { mutableStateOf("NATIONAL_ID") }
    var documentNumber by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var expandedDocType by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) step = 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Identity Verification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (step == 1) {
                        IconButton(onClick = { step = 0 }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
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
        when (step) {
            0 -> KycIntroScreen(padding = padding, onStart = { step = 1 }, onSkip = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Kyc.route) { inclusive = true }
                }
            })
            1 -> KycFormScreen(
                padding = padding,
                documentType = documentType,
                onDocumentTypeChange = { documentType = it },
                documentNumber = documentNumber,
                onDocumentNumberChange = { documentNumber = it },
                fullName = fullName,
                onFullNameChange = { fullName = it },
                dateOfBirth = dateOfBirth,
                onDateOfBirthChange = { dateOfBirth = it },
                address = address,
                onAddressChange = { address = it },
                expandedDocType = expandedDocType,
                onExpandDocType = { expandedDocType = it },
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onSubmit = {
                    viewModel.submitKyc(documentType, documentNumber, fullName, dateOfBirth, address)
                }
            )
            2 -> KycSuccessScreen(padding = padding, onContinue = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Kyc.route) { inclusive = true }
                }
            })
        }
    }
}

@Composable
private fun KycIntroScreen(padding: PaddingValues, onStart: () -> Unit, onSkip: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(GreenPrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🪪", fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("Verify Your Identity", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnSurfaceLight)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "KYC verification is required to sell energy and access full trading features. This takes less than 2 minutes.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // What you need
        val requirements = listOf(
            "📄 Government-issued ID (Passport / National ID)",
            "📅 Date of birth",
            "🏠 Residential address",
        )
        requirements.forEach { req ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(req, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = EnergyBlue.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🔒", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Your data is encrypted and stored securely. We never share your information.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnergyBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Start Verification", fontWeight = FontWeight.Bold, color = BackgroundDark)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSkip) {
            Text("Skip for now (limited access)", color = OnSurfaceMuted)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycFormScreen(
    padding: PaddingValues,
    documentType: String, onDocumentTypeChange: (String) -> Unit,
    documentNumber: String, onDocumentNumberChange: (String) -> Unit,
    fullName: String, onFullNameChange: (String) -> Unit,
    dateOfBirth: String, onDateOfBirthChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    expandedDocType: Boolean, onExpandDocType: (Boolean) -> Unit,
    isLoading: Boolean, errorMessage: String?,
    onSubmit: () -> Unit
) {
    val docTypes = listOf(
        "NATIONAL_ID" to "🪪 National ID",
        "PASSPORT" to "📘 Passport",
        "DRIVERS_LICENSE" to "🚗 Driver's License"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Personal Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurfaceLight)

        // Document Type
        ExposedDropdownMenuBox(expanded = expandedDocType, onExpandedChange = onExpandDocType) {
            OutlinedTextField(
                value = docTypes.find { it.first == documentType }?.second ?: documentType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Document Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDocType) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(14.dp),
                colors = kycFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expandedDocType,
                onDismissRequest = { onExpandDocType(false) }
            ) {
                docTypes.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label, color = OnSurfaceLight) },
                        onClick = { onDocumentTypeChange(value); onExpandDocType(false) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = documentNumber,
            onValueChange = onDocumentNumberChange,
            label = { Text("Document Number") },
            leadingIcon = { Icon(Icons.Default.Badge, null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = kycFieldColors()
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name (as on document)") },
            leadingIcon = { Icon(Icons.Default.Person, null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = kycFieldColors()
        )

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = onDateOfBirthChange,
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            leadingIcon = { Icon(Icons.Default.DateRange, null, tint = GreenPrimary) },
            placeholder = { Text("1990-01-15", color = OnSurfaceMuted) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = kycFieldColors()
        )

        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Residential Address") },
            leadingIcon = { Icon(Icons.Default.Home, null, tint = GreenPrimary) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            shape = RoundedCornerShape(14.dp),
            colors = kycFieldColors()
        )

        // Dummy test note
        Card(
            colors = CardDefaults.cardColors(containerColor = CashGold.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🧪", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "TEST MODE: KYC is auto-approved for development. Any valid-format data will work.",
                    style = MaterialTheme.typography.labelSmall,
                    color = CashGold
                )
            }
        }

        if (errorMessage != null) {
            Card(colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)), shape = RoundedCornerShape(8.dp)) {
                Text(errorMessage, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading && documentNumber.isNotBlank() && fullName.isNotBlank() && dateOfBirth.isNotBlank() && address.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Verified, null, tint = BackgroundDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Verification", fontWeight = FontWeight.Bold, color = BackgroundDark)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun KycSuccessScreen(padding: PaddingValues, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp)
            .background(Brush.radialGradient(listOf(GreenDark.copy(alpha = 0.3f), BackgroundDark))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Identity Verified!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = GreenPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Your identity has been verified successfully. You now have full access to all GreenCashX features.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VerifiedFeatureRow("✅  Sell energy on the marketplace")
                VerifiedFeatureRow("✅  Access blockchain wallet")
                VerifiedFeatureRow("✅  Trade carbon credits")
                VerifiedFeatureRow("✅  Higher transaction limits")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Go to Dashboard", fontWeight = FontWeight.Bold, color = BackgroundDark)
        }
    }
}

@Composable
private fun VerifiedFeatureRow(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)
}

@Composable
private fun kycFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenPrimary,
    unfocusedBorderColor = SurfaceVariantDark,
    focusedLabelColor = GreenPrimary,
    cursorColor = GreenPrimary,
    focusedContainerColor = CardDark,
    unfocusedContainerColor = CardDark
)
