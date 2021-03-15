package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction

fun AppState.reduce(action: Action): AppState {
    val currentlyPlaying = currentlyPlaying.reduce(action)
    val backstack = backstack.reduce(action, currentlyPlaying)
    return AppState(
        backstack = backstack,
        currentlyPlaying = currentlyPlaying,
    )
}

private fun PlaybackState?.reduce(action: Action): PlaybackState? {
    return when (action) {
        is ClickTrackAction.UpdateCurrentlyPlaying -> action.playbackState
        else -> this
    }
}
