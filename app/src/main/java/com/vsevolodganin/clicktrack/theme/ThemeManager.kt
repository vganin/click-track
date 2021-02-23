package com.vsevolodganin.clicktrack.theme

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

class ThemeManager @Inject constructor() {

    fun setTheme(theme: Theme) {
        val nightMode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Theme.AUTO -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
