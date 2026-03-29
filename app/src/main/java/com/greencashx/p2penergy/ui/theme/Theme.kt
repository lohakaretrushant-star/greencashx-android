package com.greencashx.p2penergy.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GreenCashXColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = CashGold,
    onSecondary = Color.White,
    secondaryContainer = GreenSurface,
    onSecondaryContainer = GreenDark,
    tertiary = BlockchainPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEEE0FF),
    onTertiaryContainer = Color(0xFF21005E),
    background = BackgroundDark,
    onBackground = OnSurfaceLight,
    surface = SurfaceDark,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceMuted,
    error = ErrorRed,
    onError = Color.White,
    outline = OnSurfaceMuted,
    outlineVariant = SurfaceVariantDark,
)

@Composable
fun GreenCashXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GreenCashXColorScheme,
        typography = GreenCashXTypography,
        content = content
    )
}
