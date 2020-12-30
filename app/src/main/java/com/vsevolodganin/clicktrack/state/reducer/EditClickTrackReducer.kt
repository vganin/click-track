package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.StoreUpdateClickTrack

fun Screen.EditClickTrack.reduceEditClickTrackScreen(action: Action): Screen {
    return copy(
        state = state.reduce(action)
    )
}

private fun EditClickTrackScreenState.reduce(action: Action): EditClickTrackScreenState {
    return copy(
        clickTrack = clickTrack.reduce(action),
        isErrorInName = isErrorInName.reduceIsErrorInName(action),
    )
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is StoreUpdateClickTrack.Result -> action.clickTrack
        else -> this
    }
}

private fun Boolean.reduceIsErrorInName(action: Action): Boolean {
    return when (action) {
        is StoreUpdateClickTrack.Result -> action.isErrorInName
        else -> this
    }
}
