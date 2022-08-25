package com.vsevolodganin.clicktrack.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.colorResource
import com.vsevolodganin.clicktrack.R

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    ClickTrackTheme(isSystemInDarkTheme(), content)
}

@Composable
fun ClickTrackTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) darkPalette() else lightPalette()
    MaterialTheme(colors = colors) {
        content()
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

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Composable
//private fun LightThemePreview() {
//    ThemePreview()
//}
//
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun DarkThemePreview() {
//    ThemePreview()
//}
//
//@Composable
//private fun ThemePreview() {
//    ClickTrackTheme {
//        ClickTrackListPreview()
//    }
//}
