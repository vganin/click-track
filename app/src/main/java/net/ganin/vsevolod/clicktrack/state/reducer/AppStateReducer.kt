package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.state.actions.UpdateCurrentlyPlaying

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
