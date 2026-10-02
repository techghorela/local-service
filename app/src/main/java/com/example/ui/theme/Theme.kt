package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = SwiggyOrangeDarkTheme,
    onPrimary = Color.Black,
    primaryContainer = SwiggyOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = BrandTeal,
    onSecondary = Color.Black,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
    error = BrandError
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SwiggyOrange,
    onPrimary = Color.White,
    primaryContainer = SwiggyOrangeContainer,
    onPrimaryContainer = OnSwiggyOrangeContainer,
    secondary = BrandNavy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = BrandNavy,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = NeutralLight,
    onBackground = NeutralDark,
    onSurface = NeutralDark,
    error = BrandError
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

