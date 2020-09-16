package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.TogglePlay

fun Screen.ClickTrack.reduceClickTrackScreen(action: Action): Screen {
    return Screen.ClickTrack(
        state = state.reduce(action)
    )
}

private fun ClickTrackScreenState.reduce(action: Action): ClickTrackScreenState {
    return copy(
        clickTrack = clickTrack.reduce(action),
        isPlaying = isPlaying.isPlayingReduce(action)
    )
}

private fun ClickTrack.reduce(action: Action): ClickTrack {
    return this
}

private fun Boolean.isPlayingReduce(action: Action): Boolean {
    return when (action) {
        TogglePlay -> !this
        StopPlay -> false
        else -> this
    }
}
