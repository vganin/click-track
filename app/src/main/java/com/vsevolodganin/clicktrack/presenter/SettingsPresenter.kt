package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.SettingsUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun uiScreens(): Flow<UiScreen.Settings> {
        return combine(
            userPreferencesRepository.theme.stateFlow,
            userPreferencesRepository.ignoreAudioFocus.stateFlow,
        ) { theme, ignoreAudioFocus ->
            SettingsUiState(
                theme = theme,
                ignoreAudioFocus = ignoreAudioFocus
            )
        }
            .map(UiScreen::Settings)
    }
}
