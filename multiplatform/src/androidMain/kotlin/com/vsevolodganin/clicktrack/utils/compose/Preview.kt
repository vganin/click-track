package com.vsevolodganin.clicktrack.utils.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "1. Light Theme",
    group = "themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "2. Dark Theme",
    group = "themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "3. Russian",
    locale = "ru",
)
@Preview(
    name = "4. Tablet",
    device = Devices.TABLET,
)
@Preview(
    name = "5. Tablet in Russian",
    locale = "ru",
    device = Devices.TABLET,
)
annotation class VersatilePreview

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
annotation class LightModePreview

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class DarkModePreview
