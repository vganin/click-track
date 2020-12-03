package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.PlayClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack

fun Screen.PlayClickTrack.reducePlayClickTrackScreen(action: Action, currentlyPlaying: PlaybackState?): Screen {
    return copy(
        state = state.reduce(action, currentlyPlaying)
    )
}

private fun PlayClickTrackScreenState.reduce(
    action: Action,
    currentlyPlaying: PlaybackState?
): PlayClickTrackScreenState {
    val clickTrack = clickTrack.reduce(action)
    return PlayClickTrackScreenState(clickTrack, currentlyPlaying)
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is UpdateClickTrack -> if (action.data.id == id) action.data else this
        else -> this
    }
}
