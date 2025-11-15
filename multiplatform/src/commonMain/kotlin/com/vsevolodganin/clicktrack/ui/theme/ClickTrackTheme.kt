package com.vsevolodganin.clicktrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenPreview

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) darkPalette() else lightPalette()
    MaterialTheme(colorScheme = colorScheme) {
        content()
    }
}

@Composable
private fun lightPalette() = lightColorScheme(
    primary = Colors.Signature,
    primaryContainer = Colors.SignatureAlternative,
    secondary = Colors.Signature,
    secondaryContainer = Colors.SignatureAlternative,
    background = Color(0xFFEEEDED),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
private fun darkPalette() = darkColorScheme(
    primary = Colors.Signature,
    primaryContainer = Colors.SignatureAlternative,
    secondary = Colors.Signature,
    secondaryContainer = Colors.SignatureAlternative,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private object Colors {
    val Signature = Color(0xFFA53030)
    val SignatureAlternative = Color(0xFF982D2D)
}

@Composable
internal fun ClickTrackThemePreview() {
    ClickTrackTheme {
        ClickTrackListScreenPreview()
    }
}
