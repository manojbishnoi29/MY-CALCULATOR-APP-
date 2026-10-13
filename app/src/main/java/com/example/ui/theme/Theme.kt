package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrutalistColorScheme = lightColorScheme(
    primary = BrutalistBorder,
    onPrimary = Color.White,
    primaryContainer = BrutalistCyan,
    onPrimaryContainer = BrutalistBorder,
    secondary = BrutalistYellow,
    onSecondary = BrutalistBorder,
    secondaryContainer = BrutalistPurple,
    onSecondaryContainer = BrutalistBorder,
    tertiary = BrutalistPink,
    background = BrutalistCanvas,
    onBackground = BrutalistBorder,
    surface = BrutalistSurface,
    onSurface = BrutalistBorder,
    surfaceVariant = BrutalistSurfaceVariant,
    onSurfaceVariant = BrutalistBorder,
    outline = BrutalistBorder,
    error = BrutalistPink
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrutalistColorScheme,
        typography = CalculatorTypography,
        content = content
    )
}
