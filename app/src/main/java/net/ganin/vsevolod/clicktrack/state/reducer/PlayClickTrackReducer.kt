package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlayClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.UpdateCurrentlyPlaying

fun Screen.PlayClickTrack.reducePlayClickTrackScreen(action: Action): Screen {
    return copy(
        state = state.reduce(action)
    )
}

private fun PlayClickTrackScreenState.reduce(action: Action): PlayClickTrackScreenState {
    return when (action) {
        is UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == clickTrack.id) {
            copy(playbackStamp = action.playbackState.playbackStamp, isPlaying = true)
        } else {
            copy(playbackStamp = null, isPlaying = false)
        }
        else -> copy(clickTrack = clickTrack.reduce(action))
    }
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is UpdateClickTrack -> if (action.data.id == id) action.data else this
        else -> this
    }
}
