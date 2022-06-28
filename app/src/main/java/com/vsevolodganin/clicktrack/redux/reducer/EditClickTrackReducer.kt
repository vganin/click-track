package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.redux.EditCueState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.action.EditClickTrackAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.toEditState
import com.vsevolodganin.clicktrack.utils.collection.immutable.move
import com.vsevolodganin.clicktrack.utils.collection.immutable.remove
import com.vsevolodganin.clicktrack.utils.collection.immutable.replace

fun Screen.EditClickTrack.reduceEditClickTrack(action: Action): Screen {
    return Screen.EditClickTrack(
        state = state.reduce(action)
    )
}

private fun EditClickTrackState.reduce(action: Action): EditClickTrackState {
    return when (action) {
        is EditClickTrackAction.EditName -> copy(name = action.name)
        is EditClickTrackAction.EditLoop -> copy(loop = action.loop)
        is EditClickTrackAction.AddNewCue -> copy(cues = cues + DefaultCue.toEditState())
        is EditClickTrackAction.MoveCue -> copy(cues = cues.move(action.fromIndex, action.toIndex))
        is EditClickTrackAction.RemoveCue -> copy(cues = cues.remove(action.index))
        is EditClickTrackAction.EditCueAction -> copy(cues = cues.replace(action.index) { it.reduce(action) })
        is EditClickTrackAction.SetErrors -> copy(errors = action.errors)
        else -> this
    }
}

private fun EditCueState.reduce(action: EditClickTrackAction.EditCueAction): EditCueState {
    return when (action) {
        is EditClickTrackAction.EditCueAction.EditName -> copy(name = action.name)
        is EditClickTrackAction.EditCueAction.EditBpm -> copy(bpm = action.bpm)
        is EditClickTrackAction.EditCueAction.EditTimeSignature -> copy(timeSignature = action.timeSignature)
        is EditClickTrackAction.EditCueAction.EditDuration -> when (action.duration) {
            is CueDuration.Beats -> copy(beats = action.duration)
            is CueDuration.Measures -> copy(measures = action.duration)
            is CueDuration.Time -> copy(time = action.duration)
        }
        is EditClickTrackAction.EditCueAction.EditDurationType -> copy(activeDurationType = action.durationType)
        is EditClickTrackAction.EditCueAction.EditPattern -> copy(pattern = action.pattern)
        is EditClickTrackAction.EditCueAction.SetErrors -> copy(errors = action.errors)
    }
}
