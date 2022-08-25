package com.vsevolodganin.clicktrack.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val userPreferences: UserPreferencesRepository,
) : SettingsViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<SettingsState> = combine(
        userPreferences.theme.flow,
        userPreferences.ignoreAudioFocus.flow,
    ) { theme, ignoreAudioFocus ->
        SettingsState(
            theme = theme,
            ignoreAudioFocus = ignoreAudioFocus
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsState(
            theme = userPreferences.theme.value,
            ignoreAudioFocus = userPreferences.ignoreAudioFocus.value
        )
    )

    override fun onBackClick() = navigation.pop()

    override fun onThemeChange(theme: Theme) {
        userPreferences.theme.value = theme
    }

    override fun onIgnoreAudioFocusChange(ignoreAudioFocus: Boolean) {
        userPreferences.ignoreAudioFocus.value = ignoreAudioFocus
    }
}
