package com.lightledger.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val LightLedgerColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF050505),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF1F1EF),
    onPrimaryContainer = Color(0xFF050505),
    secondary = Color(0xFF191919),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDEDEA),
    onSecondaryContainer = Color(0xFF191919),
    tertiary = Color(0xFF747474),
    surface = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7F5),
    surfaceContainer = Color(0xFFEDEDEA),
    background = Color(0xFFF7F7F5),
    onBackground = Color(0xFF050505),
    onSurface = Color(0xFF050505),
    onSurfaceVariant = Color(0xFF747474),
    outline = Color(0xFFE6E6E2),
)

private val LightLedgerShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

private val LightLedgerTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontWeight = FontWeight.SemiBold),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.Medium),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.Medium),
    )
}

@Composable
fun LightLedgerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightLedgerColorScheme,
        typography = LightLedgerTypography,
        shapes = LightLedgerShapes,
        content = content,
    )
}
