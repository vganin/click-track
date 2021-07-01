package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.SettingsUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Reusable
class SettingsPresenter @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun uiScreens(): Flow<UiScreen.Settings> {
        return userPreferencesRepository.themeFlow
            .map { theme -> SettingsUiState(theme = theme) }
            .map(UiScreen::Settings)
    }
}
