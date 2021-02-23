package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.MetronomeActions
import com.vsevolodganin.clicktrack.state.actions.StartPlay
import com.vsevolodganin.clicktrack.state.actions.UpdateCurrentlyPlaying

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return Screen.Metronome(
        state = state.reduce(action)
    )
}

private fun MetronomeScreenState?.reduce(action: Action): MetronomeScreenState? {
    return when (action) {
        is MetronomeActions.SetScreenState -> action.state
        is MetronomeActions.ChangeBpm -> this?.copy(bpm = action.bpm)
        is StartPlay -> if (action.clickTrack.id == MetronomeId) {
            this?.copy(isPlaying = true)
        } else {
            this
        }
        com.vsevolodganin.clicktrack.state.actions.StopPlay -> this?.copy(isPlaying = false)
        is UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == MetronomeId) {
            this?.copy(progress = action.playbackState.progress, isPlaying = true)
        } else {
            this?.copy(progress = null, isPlaying = false)
        }
        else -> this
    }
}
