package com.vsevolodganin.clicktrack.settings

import com.vsevolodganin.clicktrack.theme.Theme

data class SettingsState(
    val theme: Theme,
    val ignoreAudioFocus: Boolean,
)
