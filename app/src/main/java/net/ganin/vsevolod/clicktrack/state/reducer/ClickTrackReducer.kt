package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.lib.SerializableDuration
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackTimestamp

fun Screen.ClickTrack.reduceClickTrackScreen(action: Action): Screen {
    return Screen.ClickTrack(
        state = state.reduce(action)
    )
}

private fun ClickTrackScreenState.reduce(action: Action): ClickTrackScreenState {
    val isPlaying = isPlaying.reduce(action)
    return copy(
        isPlaying = isPlaying,
        playbackTimestamp = playbackTimestamp.reduce(action, isPlaying)
    )
}

private fun Boolean.reduce(action: Action): Boolean {
    return when (action) {
        TogglePlay -> !this
        StopPlay -> false
        else -> this
    }
}

private fun SerializableDuration?.reduce(action: Action, isPlaying: Boolean): SerializableDuration? {
    return when {
        !isPlaying -> null
        action is UpdatePlaybackTimestamp -> SerializableDuration(action.value)
        else -> this
    }
}
