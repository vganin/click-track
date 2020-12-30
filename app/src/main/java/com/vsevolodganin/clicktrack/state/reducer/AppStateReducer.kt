package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.actions.UpdateCurrentlyPlaying

fun AppState.reduce(action: Action): AppState {
    val currentlyPlaying = currentlyPlaying.reduce(action)
    return copy(
        backstack = backstack.reduce(action, currentlyPlaying),
        currentlyPlaying = currentlyPlaying
    )
}

private fun PlaybackState?.reduce(action: Action): PlaybackState? {
    return when (action) {
        is UpdateCurrentlyPlaying -> action.playbackState
        else -> this
    }
}
