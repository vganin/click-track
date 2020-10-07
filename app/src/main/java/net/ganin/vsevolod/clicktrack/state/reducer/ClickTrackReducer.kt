package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackDataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackStamp

fun Screen.ClickTrack.reduceClickTrackScreen(action: Action): Screen {
    return Screen.ClickTrack(
        state = state.reduce(action)
    )
}

private fun ClickTrackScreenState.reduce(action: Action): ClickTrackScreenState {
    val isPlaying = isPlaying.reduce(action)
    return copy(
        clickTrack = clickTrack.reduce(action),
        isPlaying = isPlaying,
        playbackStamp = playbackStamp.reduce(action, isPlaying)
    )
}

private fun ClickTrackWithMeta.reduce(action: Action): ClickTrackWithMeta {
    return when (action) {
        is ClickTrackDataLoadedAction -> if (action.data.name == name) action.data else this
        else -> this
    }
}

private fun Boolean.reduce(action: Action): Boolean {
    return when (action) {
        TogglePlay -> !this
        StopPlay -> false
        else -> this
    }
}

private fun PlaybackStamp?.reduce(action: Action, isPlaying: Boolean): PlaybackStamp? {
    return when {
        !isPlaying -> null
        action is UpdatePlaybackStamp -> action.value
        else -> this
    }
}
