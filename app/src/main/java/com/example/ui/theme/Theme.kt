package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = NeonOrange,
  onPrimary = Color.White,
  primaryContainer = DarkOperatorKey,
  onPrimaryContainer = Color.White,
  secondary = NeonCyan,
  onSecondary = Color.Black,
  secondaryContainer = DarkFunctionKey,
  onSecondaryContainer = NeonCyan,
  tertiary = NeonTeal,
  background = DarkBackground,
  onBackground = TextPrimaryDark,
  surface = DarkSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  error = ErrorRed,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = NeonOrange,
  onPrimary = Color.White,
  primaryContainer = LightOperatorKey,
  onPrimaryContainer = Color.White,
  secondary = Color(0xFF0284C7),
  onSecondary = Color.White,
  secondaryContainer = LightFunctionKey,
  onSecondaryContainer = Color(0xFF0369A1),
  tertiary = Color(0xFF0F766E),
  background = LightBackground,
  onBackground = TextPrimaryLight,
  surface = LightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = TextSecondaryLight,
  error = ErrorRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our tailored calculator palette for maximum contrast
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
