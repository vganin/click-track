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
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.ui.piece.SystemUiSetup
import com.vsevolodganin.clicktrack.ui.piece.darkAppBar
import com.vsevolodganin.clicktrack.utils.compose.DarkPreview
import com.vsevolodganin.clicktrack.utils.compose.LightPreview
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape
import com.vsevolodganin.clicktrack.utils.compose.navigationBars
import dev.icerock.moko.resources.compose.colorResource

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
private fun lightPalette() =
    lightColors(
        primary = colorResource(MR.colors.primary),
        primaryVariant = colorResource(MR.colors.primary_variant),
        secondary = colorResource(MR.colors.secondary),
        secondaryVariant = colorResource(MR.colors.secondary_variant),
        background = colorResource(MR.colors.background),
        surface = colorResource(MR.colors.surface),
        onPrimary = colorResource(MR.colors.on_primary),
        onSecondary = colorResource(MR.colors.on_secondary),
        onBackground = colorResource(MR.colors.on_background),
        onSurface = colorResource(MR.colors.on_surface),
    )

@Composable
private fun darkPalette() =
    darkColors(
        primary = colorResource(MR.colors.primary),
        primaryVariant = colorResource(MR.colors.primary_variant),
        secondary = colorResource(MR.colors.secondary),
        secondaryVariant = colorResource(MR.colors.secondary_variant),
        background = colorResource(MR.colors.background),
        surface = colorResource(MR.colors.surface),
        onPrimary = colorResource(MR.colors.on_primary),
        onSecondary = colorResource(MR.colors.on_secondary),
        onBackground = colorResource(MR.colors.on_background),
        onSurface = colorResource(MR.colors.on_surface),
    )

// Taken for Surface.kt
@Composable
private fun surfaceColorAtElevation(
    color: Color,
    elevationOverlay: ElevationOverlay?,
    absoluteElevation: Dp,
): Color {
    return if (color == MaterialTheme.colors.surface && elevationOverlay != null) {
        elevationOverlay.apply(color, absoluteElevation)
    } else {
        color
    }
}

@LightPreview
@Composable
private fun LightThemePreview() {
    ThemePreview()
}

@DarkPreview
@Composable
private fun DarkThemePreview() {
    ThemePreview()
}

@Composable
private fun ThemePreview() {
    ClickTrackTheme {
        // TODO: Uncomment after porting ClickTrackListPreview
//        ClickTrackListPreview()
    }
}
