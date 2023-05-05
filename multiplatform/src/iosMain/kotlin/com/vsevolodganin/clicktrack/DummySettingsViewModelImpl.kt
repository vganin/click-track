package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.settings.SettingsState
import com.vsevolodganin.clicktrack.settings.SettingsViewModel
import com.vsevolodganin.clicktrack.theme.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummySettingsViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : SettingsViewModel {
    override val state: StateFlow<SettingsState> = MutableStateFlow(
        SettingsState(
            theme = Theme.SYSTEM,
            ignoreAudioFocus = false,
            language = AppLanguage.SYSTEM,
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onThemeChange(theme: Theme) = Unit
    override fun onLanguageChange(language: AppLanguage) = Unit
    override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) = Unit
}
