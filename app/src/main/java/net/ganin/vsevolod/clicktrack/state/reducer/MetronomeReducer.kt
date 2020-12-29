package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.model.MetronomeId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.MetronomeScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.MetronomeActions
import net.ganin.vsevolod.clicktrack.state.actions.StartPlay
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdateCurrentlyPlaying

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return copy(
        state = state.reduce(action)
    )
}

private fun MetronomeScreenState?.reduce(action: Action): MetronomeScreenState? {
    return when (action) {
        is MetronomeActions.UpdateMetronomeState -> action.state
        is MetronomeActions.ChangeBpm -> this?.copy(bpm = action.bpm)
        is StartPlay -> if (action.clickTrack.id == MetronomeId) {
            this?.copy(isPlaying = true)
        } else {
            this
        }
        StopPlay -> this?.copy(isPlaying = false)
        is UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == MetronomeId) {
            this?.copy(progress = action.playbackState.progress, isPlaying = true)
        } else {
            this?.copy(progress = null, isPlaying = false)
        }
        else -> this
    }
}
