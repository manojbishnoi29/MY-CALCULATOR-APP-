package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = SurfaceElevatedDark,
    onPrimaryContainer = AccentCyan,
    secondary = AccentAmber,
    onSecondary = Color(0xFF1E1F25),
    secondaryContainer = SurfaceHighlightDark,
    onSecondaryContainer = AccentAmber,
    tertiary = AccentPurple,
    background = BackgroundDark,
    onBackground = PrimaryText,
    surface = SurfaceDark,
    onSurface = PrimaryText,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = SecondaryText,
    outline = Color(0xFF334155),
    error = AccentRose
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFFB45309),
    tertiary = Color(0xFF6366F1),
    background = BackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFE11D48)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional aesthetic styling by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // default to sleek dark design for calculator feel or toggleable

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CalculatorTypography,
        content = content
    )
}
