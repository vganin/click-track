package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.TrainingState
import com.vsevolodganin.clicktrack.redux.action.TrainingAction
import com.vsevolodganin.clicktrack.redux.core.Action

fun Screen.Training.reduceTraining(action: Action): Screen {
    return copy(state = state.reduce(action))
}

private fun TrainingState.reduce(action: Action): TrainingState {
    return when (action) {
        is TrainingAction.EditStartingTempo -> copy(startingTempo = action.tempo)
        is TrainingAction.EditMode -> copy(mode = action.mode)
        is TrainingAction.EditSegmentLength -> when (val segmentLength = action.segmentLength) {
            is CueDuration.Beats -> copy(segmentLengthBeats = segmentLength)
            is CueDuration.Measures -> copy(segmentLengthMeasures = segmentLength)
            is CueDuration.Time -> copy(segmentLengthTime = segmentLength)
        }
        is TrainingAction.EditSegmentLengthType -> copy(activeSegmentLengthType = action.segmentLengthType)
        is TrainingAction.EditTempoChange -> copy(tempoChange = action.tempo)
        is TrainingAction.EditEnding -> when (val ending = action.ending) {
            is TrainingState.Ending.ByTempo -> copy(endingByTempo = ending)
            is TrainingState.Ending.ByTime -> copy(endingByTime = ending)
        }
        is TrainingAction.EditEndingKind -> copy(activeEndingKind = action.endingKind)
        is TrainingAction.UpdateErrors -> copy(errors = action.errors)
        else -> this
    }
}
