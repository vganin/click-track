package com.vsevolodganin.clicktrack.view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.vsevolodganin.clicktrack.R

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkPalette() else lightPalette()
    MaterialTheme(colors = colors) {
        content()
    }
}

@Composable
private fun lightPalette() = lightColors(
    primary = colorResource(R.color.primary),
    primaryVariant = colorResource(R.color.primary_dark),
    secondary = colorResource(R.color.secondary),
    secondaryVariant = colorResource(R.color.secondary_dark),
    background = colorResource(R.color.blue_gray100),
    surface = colorResource(R.color.blue_gray50)
)

@Composable
private fun darkPalette() = darkColors(
    primary = colorResource(R.color.primary),
    primaryVariant = colorResource(R.color.primary_dark),
    secondary = colorResource(R.color.secondary),
)
