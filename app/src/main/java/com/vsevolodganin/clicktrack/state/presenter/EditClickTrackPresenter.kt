package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Reusable
class EditClickTrackPresenter @Inject constructor() {

    fun uiScreens(screens: Flow<Screen.EditClickTrack>): Flow<UiScreen.EditClickTrack> {
        return screens
            .map { it.state }
            .map { state ->
                EditClickTrackUiState(
                    clickTrack = state.clickTrack,
                    defaultCue = DefaultCue,
                    hasErrorInName = state.hasErrorInName
                )
            }
            .map(UiScreen::EditClickTrack)
    }
}
