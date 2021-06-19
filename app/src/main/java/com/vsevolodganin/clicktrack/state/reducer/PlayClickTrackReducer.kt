package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.screen.PlayClickTrackScreenState
import com.vsevolodganin.clicktrack.state.screen.Screen

fun Screen.PlayClickTrack.reducePlayClickTrackScreen(action: Action): Screen {
    return Screen.PlayClickTrack(
        state = state.reduce(action)
    )
}

private fun PlayClickTrackScreenState.reduce(action: Action): PlayClickTrackScreenState {
    return when (action) {
        is ClickTrackAction.UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == clickTrack.id) {
            copy(progress = ClickTrackProgress(action.playbackState.progress), isPlaying = true)
        } else {
            copy(progress = null, isPlaying = false)
        }
        else -> copy(clickTrack = clickTrack.reduce(action))
    }
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is ClickTrackAction.UpdateClickTrack -> if (action.data.id == id) action.data else this
        else -> this
    }
}
