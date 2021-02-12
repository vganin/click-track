package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat

@Composable
fun String.toUpperCase(): String {
    return toUpperCase(ConfigurationCompat.getLocales(LocalConfiguration.current)[0])
}
