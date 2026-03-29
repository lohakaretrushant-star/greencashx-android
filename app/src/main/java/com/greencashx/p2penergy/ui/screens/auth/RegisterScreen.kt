package com.greencashx.p2penergy.ui.screens.auth

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greencashx.p2penergy.ui.theme.*
import com.greencashx.p2penergy.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var hasSolarPanel by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(GreenDark.copy(alpha = 0.25f), BackgroundDark)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceLight
            )
            Text(
                "Join the clean energy revolution",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input fields
            listOf(
                Triple("Full Name", Icons.Default.Person, fullName),
                Triple("Email Address", Icons.Default.Email, email),
                Triple("Phone Number", Icons.Default.Phone, phone),
            ).forEachIndexed { index, (label, icon, value) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        when (index) {
                            0 -> fullName = it
                            1 -> email = it
                            2 -> phone = it
                        }
                        localError = null; viewModel.clearError()
                    },
                    label = { Text(label) },
                    leadingIcon = { Icon(icon, contentDescription = null, tint = GreenPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (index) {
                            1 -> KeyboardType.Email
                            2 -> KeyboardType.Phone
                            else -> KeyboardType.Text
                        }
                    ),
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
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; localError = null; viewModel.clearError() },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = OnSurfaceMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; localError = null },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null, tint = GreenPrimary) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Solar panel toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("☀️", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "I have solar panels",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnSurfaceLight
                        )
                        Text(
                            "Enable energy selling features",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceMuted
                        )
                    }
                    Switch(
                        checked = hasSolarPanel,
                        onCheckedChange = { hasSolarPanel = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenDark)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                )
                Text(
                    "I agree to the Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceMuted
                )
            }

            val displayError = localError ?: uiState.errorMessage
            if (displayError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)), shape = RoundedCornerShape(8.dp)) {
                    Text(displayError, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        fullName.isBlank() || email.isBlank() || password.isBlank() ->
                            localError = "Please fill in all required fields"
                        password != confirmPassword ->
                            localError = "Passwords do not match"
                        password.length < 8 ->
                            localError = "Password must be at least 8 characters"
                        !agreedToTerms ->
                            localError = "Please accept the terms to continue"
                        else -> viewModel.register(fullName, email, password, phone.ifBlank { null }, hasSolarPanel)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = BackgroundDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Create My Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BackgroundDark)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = OnSurfaceMuted, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onLogin) {
                    Text("Sign In", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
