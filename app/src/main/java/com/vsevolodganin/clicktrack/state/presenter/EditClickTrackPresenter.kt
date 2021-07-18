package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.ui.model.EditClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.EditCueUiState
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
                    name = state.name,
                    loop = state.loop,
                    cues = state.cues.map { it.toUiState() },
                    errors = state.errors,
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
