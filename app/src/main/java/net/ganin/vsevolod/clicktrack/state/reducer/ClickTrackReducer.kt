package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlayClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackStamp
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackDataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.ResetPlaybackStamp
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdatePlaybackStamp

fun Screen.PlayClickTrack.reducePlayClickTrackScreen(action: Action): Screen {
    return Screen.PlayClickTrack(
        state = state.reduce(action)
    )
}

private fun PlayClickTrackScreenState.reduce(action: Action): PlayClickTrackScreenState {
    val isPlaying = isPlaying.reduce(action)
    return copy(
        clickTrack = clickTrack.reduce(action),
        isPlaying = isPlaying,
        playbackStamp = playbackStamp.reduce(action, isPlaying)
    )
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is ClickTrackDataLoadedAction -> if (action.data.id == id) action.data else this
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
        action is ResetPlaybackStamp -> null
        else -> this
    }
}
