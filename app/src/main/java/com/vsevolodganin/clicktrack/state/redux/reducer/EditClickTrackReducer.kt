package com.vsevolodganin.clicktrack.state.redux.reducer

import com.vsevolodganin.clicktrack.state.redux.EditClickTrackState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.core.Action

fun Screen.EditClickTrack.reduceEditClickTrackScreen(action: Action): Screen {
    return Screen.EditClickTrack(
        state = state.reduce(action)
    )
}

private fun EditClickTrackState.reduce(action: Action): EditClickTrackState {
    return when (action) {
        is ClickTrackAction.UpdateClickTrack -> {
            copy(clickTrack = action.clickTrack)
        }
        is ClickTrackAction.UpdateErrorInName ->
            if (action.id == clickTrack.id) {
                copy(hasErrorInName = action.isPresent)
            } else {
                this
            }
        else -> this
    }
}
