package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.EditClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen

fun Screen.EditClickTrack.reduceEditClickTrackScreen(action: Action): Screen {
    return Screen.EditClickTrack(
        state = state.reduce(action)
    )
}

private fun EditClickTrackScreenState.reduce(action: Action): EditClickTrackScreenState {
    return this // TODO
}
