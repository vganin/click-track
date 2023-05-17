package com.vsevolodganin.clicktrack.settings

import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.theme.Theme
import kotlinx.coroutines.flow.StateFlow

interface SettingsViewModel {
    val state: StateFlow<SettingsState>
    fun onBackClick()
    fun onThemeChange(theme: Theme)
    fun onLanguageChange(language: AppLanguage)
    fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean)
    fun onKotlinCrashClick()
    fun onNativeCrashClick()
}
