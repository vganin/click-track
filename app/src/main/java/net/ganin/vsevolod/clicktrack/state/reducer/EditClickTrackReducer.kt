package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.EditClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack

fun Screen.EditClickTrack.reduceEditClickTrackScreen(action: Action): Screen {
    return Screen.EditClickTrack(
        state = state.reduce(action)
    )
}

private fun EditClickTrackScreenState.reduce(action: Action): EditClickTrackScreenState {
    return copy(
        clickTrack = clickTrack.reduce(action)
    )
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is UpdateClickTrack -> action.clickTrack
        else -> this
    }
}
