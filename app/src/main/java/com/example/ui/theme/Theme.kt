package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = AmberPrimary,
    onPrimary = DarkBg,
    primaryContainer = AmberContainerDark,
    onPrimaryContainer = AmberLight,
    secondary = BcvBlue,
    onSecondary = DarkBg,
    secondaryContainer = BcvBlueContainer,
    onSecondaryContainer = BcvBlueLight,
    tertiary = IntervencionPurple,
    onTertiary = DarkBg,
    tertiaryContainer = IntervencionContainer,
    onTertiaryContainer = IntervencionLight,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = SpreadRed,
    onError = DarkBg,
    errorContainer = SpreadRedContainer,
    onErrorContainer = SpreadRedLight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AmberDark,
    onPrimary = LightSurface,
    primaryContainer = AmberContainerLight,
    onPrimaryContainer = AmberDark,
    secondary = BcvBlueDark,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = BcvBlueDark,
    tertiary = IntervencionPurple,
    onTertiary = LightSurface,
    tertiaryContainer = LightSurfaceVariant,
    onTertiaryContainer = IntervencionPurple,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = SpreadRed,
    onError = LightSurface,
    errorContainer = SpreadRedLight,
    onErrorContainer = SpreadRedContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Enable custom stylish fintech colors by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

