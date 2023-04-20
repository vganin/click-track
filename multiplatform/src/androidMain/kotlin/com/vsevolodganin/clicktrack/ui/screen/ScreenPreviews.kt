package com.vsevolodganin.clicktrack.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "1. Light Theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    name = "2. Dark Theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES
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
actual annotation class ScreenPreviews
