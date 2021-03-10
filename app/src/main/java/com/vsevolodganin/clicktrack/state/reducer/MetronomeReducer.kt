package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return Screen.Metronome(
        state = state.reduce(action)
    )
}

private fun MetronomeScreenState?.reduce(action: Action): MetronomeScreenState? {
    return when (action) {
        is MetronomeAction.SetScreenState -> action.state
        is MetronomeAction.ChangeBpm -> this?.copy(bpm = action.bpm)
        is ClickTrackAction.StartPlay -> if (action.clickTrack.id == MetronomeId) {
            this?.copy(isPlaying = true)
        } else {
            this
        }
        ClickTrackAction.StopPlay -> this?.copy(isPlaying = false)
        is ClickTrackAction.UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == MetronomeId) {
            this?.copy(progress = action.playbackState.progress, isPlaying = true)
        } else {
            this?.copy(progress = null, isPlaying = false)
        }
        else -> this
    }
}
