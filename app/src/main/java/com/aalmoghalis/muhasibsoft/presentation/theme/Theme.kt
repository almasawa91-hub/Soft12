package com.aalmoghalis.muhasibsoft.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// الألوان
val PrimaryColor = Color(0xFF1976D2)
val PrimaryDarkColor = Color(0xFF1565C0)
val SecondaryColor = Color(0xFF4CAF50)
val BackgroundColor = Color(0xFFFAFAFA)
val SurfaceColor = Color(0xFFFFFFFF)
val ErrorColor = Color(0xFFD32F2F)
val WarningColor = Color(0xFFFF9800)
val SuccessColor = Color(0xFF4CAF50)
val TextPrimaryColor = Color(0xFF212121)
val TextSecondaryColor = Color(0xFF757575)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = PrimaryDarkColor,
    onPrimaryContainer = Color.White,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SecondaryColor.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryColor,
    background = BackgroundColor,
    onBackground = TextPrimaryColor,
    surface = SurfaceColor,
    onSurface = TextPrimaryColor,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = TextSecondaryColor,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.2f),
    onErrorContainer = ErrorColor
)

@Composable
fun MuhasibSoftTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryDarkColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}