package com.vsevolodganin.clicktrack.theme

import androidx.appcompat.app.AppCompatDelegate
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class ThemeManager(
    private val userPreferences: UserPreferencesRepository,
) {
    fun start() {
        setTheme(userPreferences.theme.value)
        GlobalScope.launch(context = Dispatchers.Main) {
            userPreferences.theme.flow.collectLatest { theme ->
                setTheme(theme)
            }
        }
    }

    private fun setTheme(theme: Theme) {
        val nightMode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Theme.AUTO -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
