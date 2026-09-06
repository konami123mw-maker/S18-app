package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color(0xFF031024),
    primaryContainer = Color(0xFF0E3A5A),
    onPrimaryContainer = Color(0xFFE0F7FA),
    secondary = CyberElectricBlue,
    onSecondary = Color(0xFF031024),
    tertiary = CyberMint,
    background = S18DarkBackground,
    onBackground = S18DarkOnBackground,
    surface = S18DarkSurface,
    onSurface = S18DarkOnSurface,
    surfaceVariant = S18DarkSurfaceVariant,
    onSurfaceVariant = S18DarkOnSurfaceVariant,
    outline = S18DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = CyanDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = CyberElectricBlue,
    onSecondary = Color.White,
    tertiary = CyberMint,
    background = S18LightBackground,
    onBackground = S18LightOnBackground,
    surface = S18LightSurface,
    onSurface = S18LightOnSurface,
    surfaceVariant = S18LightSurfaceVariant,
    onSurfaceVariant = S18LightOnSurfaceVariant,
    outline = S18LightOutline
)

@Composable
fun S18Theme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backwards compatibility with template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    S18Theme(darkTheme = darkTheme, content = content)
}
