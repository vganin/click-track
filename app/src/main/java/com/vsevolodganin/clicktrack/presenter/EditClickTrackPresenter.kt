package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EditClickTrackPresenter @Inject constructor() {

    fun uiScreens(screens: Flow<Screen.EditClickTrack>): Flow<UiScreen.EditClickTrack> {
        return screens
            .map { it.state }
            .map { state ->
                EditClickTrackUiState(
                    id = state.id,
                    name = state.name,
                    loop = state.loop,
                    cues = state.cues.map { it.toUiState() },
                    errors = state.errors,
                    showForwardButton = state.isInitialEdit,
                )
            }
            .map(UiScreen::EditClickTrack)
    }

    private fun EditCueState.toUiState() = EditCueUiState(
        id = id,
        name = name,
        bpm = bpm,
        timeSignature = timeSignature,
        duration = when (activeDurationType) {
            EditCueState.DurationType.BEATS -> beats
            EditCueState.DurationType.MEASURES -> measures
            EditCueState.DurationType.TIME -> time
        },
        pattern = pattern,
        errors = errors,
    )
}
