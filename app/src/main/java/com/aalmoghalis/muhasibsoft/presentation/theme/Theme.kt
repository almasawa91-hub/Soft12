package com.aalmoghalis.muhasibsoft.presentation.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val MuhasibColors = lightColorScheme(
    primary = Color(0xFF1976D2), onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E8FC), onPrimaryContainer = Color(0xFF102D4C),
    secondary = Color(0xFF16845B), onSecondary = Color.White,
    background = Color(0xFFF6F8FB), onBackground = Color(0xFF17202A),
    surface = Color.White, onSurface = Color(0xFF17202A), error = Color(0xFFB3261E)
)
@Composable
fun MuhasibSoftTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MuhasibColors, content = content)
}
