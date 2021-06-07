package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction

fun Screen.EditClickTrack.reduceEditClickTrackScreen(action: Action): Screen {
    return Screen.EditClickTrack(
        state = state.reduce(action)
    )
}

private fun EditClickTrackScreenState.reduce(action: Action): EditClickTrackScreenState {
    val clickTrack = clickTrack.reduce(action)
    return copy(
        clickTrack = clickTrack,
        isErrorInName = isErrorInName.reduceIsErrorInName(action, clickTrack.id),
    )
}

private fun ClickTrackWithId.reduce(action: Action): ClickTrackWithId {
    return when (action) {
        is ClickTrackAction.UpdateClickTrack -> action.data
        else -> this
    }
}

private fun Boolean.reduceIsErrorInName(action: Action, id: ClickTrackId): Boolean {
    return when {
        action is ClickTrackAction.UpdateErrorInName && action.id == id -> action.isPresent
        else -> this
    }
}
