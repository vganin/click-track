package com.vsevolodganin.clicktrack.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ElevationOverlay
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vsevolodganin.clicktrack.ui.piece.SystemUiSetup
import com.vsevolodganin.clicktrack.ui.piece.darkAppBar
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenPreview
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape
import com.vsevolodganin.clicktrack.utils.compose.navigationBars

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    SystemUiSetup()

    val colors = if (isSystemInDarkTheme()) darkPalette() else lightPalette()
    MaterialTheme(colors = colors) {
        // Draw hard navigation bar if in landscape mode. This prevents quirky offsets from horizontal borders of screen
        if (isSystemInLandscape()) {
            // TODO: However this makes no sense for gesture navigation as it appears at the bottom of the screen even in landscape.
            // Probably should detect from which side navigation bar is coming and specifically workaround only horizontal directions.
            Box(
                modifier = Modifier
                    .background(
                        surfaceColorAtElevation(
                            color = MaterialTheme.colors.darkAppBar,
                            elevationOverlay = LocalElevationOverlay.current,
                            absoluteElevation = AppBarDefaults.TopAppBarElevation,
                        ),
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars),
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
private fun lightPalette() = lightColors(
    primary = Color(0xFFa53030), // signature
    primaryVariant = Color(0xFF982d2d), // signature_dark
    secondary = Color(0xFFa53030), // signature
    secondaryVariant = Color(0xFF982d2d), // signature_dark
    background = Color(0xFFeeeded), // light background
    surface = Color(0xFFffffff), // white
    onPrimary = Color(0xFFffffff), // white
    onSecondary = Color(0xFFffffff), // white
    onBackground = Color(0xFF000000), // black
    onSurface = Color(0xFF000000), // black
)

@Composable
private fun darkPalette() = darkColors(
    primary = Color(0xFFa53030), // signature
    primaryVariant = Color(0xFF982d2d), // signature_dark
    secondary = Color(0xFFa53030), // signature  
    secondaryVariant = Color(0xFF982d2d), // signature_dark
    background = Color(0xFF121212), // dark background
    surface = Color(0xFF121212), // dark surface
    onPrimary = Color(0xFFffffff), // white
    onSecondary = Color(0xFFffffff), // white
    onBackground = Color(0xFFffffff), // white
    onSurface = Color(0xFFffffff), // white
)

// Taken for Surface.kt
@Composable
private fun surfaceColorAtElevation(color: Color, elevationOverlay: ElevationOverlay?, absoluteElevation: Dp): Color {
    return if (color == MaterialTheme.colors.surface && elevationOverlay != null) {
        elevationOverlay.apply(color, absoluteElevation)
    } else {
        color
    }
}

@Composable
internal fun ClickTrackThemePreview() {
    ClickTrackTheme {
        ClickTrackListScreenPreview()
    }
}
