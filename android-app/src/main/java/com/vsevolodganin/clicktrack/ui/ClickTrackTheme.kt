package com.vsevolodganin.clicktrack.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ElevationOverlay
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.ui.piece.darkAppBar
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListPreview
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    ClickTrackTheme(isSystemInDarkTheme(), isSystemInLandscape(), content)
}

@Composable
fun ClickTrackTheme(
    isDarkTheme: Boolean,
    isLandscape: Boolean,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, isDarkTheme) {
        systemUiController.apply {
            setStatusBarColor(
                color = Color.Transparent,
                darkIcons = false,
            )
            setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = !isDarkTheme && !isLandscape,
                navigationBarContrastEnforced = false
            )
        }
        onDispose {}
    }

    val colors = if (isDarkTheme) darkPalette() else lightPalette()
    MaterialTheme(colors = colors) {
        // Draw hard navigation bar if in landscape mode. This prevents quirky offsets from horizontal borders of screen
        if (isLandscape) {
            // TODO: However this makes no sense for gesture navigation as it appears at the bottom of the screen even in landscape.
            // Probably should detect from which side navigation bar is coming and specifically workaround only horizontal directions.
            Box(
                modifier = Modifier
                    .background(
                        surfaceColorAtElevation(
                            color = MaterialTheme.colors.darkAppBar,
                            elevationOverlay = LocalElevationOverlay.current,
                            absoluteElevation = AppBarDefaults.TopAppBarElevation
                        )
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
@ReadOnlyComposable
private fun lightPalette() = lightColors(
    primary = colorResource(R.color.primary),
    primaryVariant = colorResource(R.color.primary_variant),
    secondary = colorResource(R.color.secondary),
    secondaryVariant = colorResource(R.color.secondary_variant),
    background = colorResource(R.color.background),
    surface = colorResource(R.color.surface),
    onPrimary = colorResource(R.color.on_primary),
    onSecondary = colorResource(R.color.on_secondary),
    onBackground = colorResource(R.color.on_background),
    onSurface = colorResource(R.color.on_surface),
)

@Composable
@ReadOnlyComposable
private fun darkPalette() = darkColors(
    primary = colorResource(R.color.primary),
    primaryVariant = colorResource(R.color.primary_variant),
    secondary = colorResource(R.color.secondary),
    secondaryVariant = colorResource(R.color.secondary_variant),
    background = colorResource(R.color.background),
    surface = colorResource(R.color.surface),
    onPrimary = colorResource(R.color.on_primary),
    onSecondary = colorResource(R.color.on_secondary),
    onBackground = colorResource(R.color.on_background),
    onSurface = colorResource(R.color.on_surface),
)

// Taken for Surface.kt
@Composable
private fun surfaceColorAtElevation(
    color: Color,
    elevationOverlay: ElevationOverlay?,
    absoluteElevation: Dp
): Color {
    return if (color == MaterialTheme.colors.surface && elevationOverlay != null) {
        elevationOverlay.apply(color, absoluteElevation)
    } else {
        color
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LightThemePreview() {
    ThemePreview()
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkThemePreview() {
    ThemePreview()
}

@Composable
private fun ThemePreview() {
    ClickTrackTheme {
        ClickTrackListPreview()
    }
}
