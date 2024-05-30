package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vsevolodganin.clicktrack.utils.compose.isSystemInLandscape

@Composable
actual fun SystemUiSetup() {
    // TODO: Support edge-to-edge mode
    @Suppress("DEPRECATION")
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val isLandscape = isSystemInLandscape()
    DisposableEffect(systemUiController, isDarkTheme, isLandscape) {
        systemUiController.apply {
            setStatusBarColor(
                color = Color.Transparent,
                darkIcons = false,
            )
            setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = !isDarkTheme && !isLandscape,
                navigationBarContrastEnforced = false,
            )
        }
        onDispose {}
    }
}
