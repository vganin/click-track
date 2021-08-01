package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.state.redux.EditCueState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.TrainingState
import com.vsevolodganin.clicktrack.ui.model.TrainingUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Reusable
class TrainingPresenter @Inject constructor() {

    fun uiScreens(screens: Flow<Screen.Training>): Flow<UiScreen.Training> {
        return screens
            .map { it.state }
            .map { state ->
                TrainingUiState(
                    startingTempo = state.startingTempo,
                    mode = state.mode,
                    segmentLength = when (state.activeSegmentLengthType) {
                        EditCueState.DurationType.BEATS -> state.segmentLengthBeats
                        EditCueState.DurationType.MEASURES -> state.segmentLengthMeasures
                        EditCueState.DurationType.TIME -> state.segmentLengthTime
                    },
                    tempoChange = state.tempoChange,
                    ending = when (state.activeEndingKind) {
                        TrainingState.EndingKind.BY_TEMPO -> state.endingByTempo
                        TrainingState.EndingKind.BY_TIME -> state.endingByTime
                    },
                    errors = state.errors,
                )
            }
            .map(UiScreen::Training)
    }
}
