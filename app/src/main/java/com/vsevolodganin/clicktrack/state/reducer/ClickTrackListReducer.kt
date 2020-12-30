package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.UpdateClickTrackList

fun Screen.ClickTrackList.reduceClickTrackListScreen(action: Action): Screen {
    return copy(
        state = state.reduce(action)
    )
}

private fun ClickTrackListScreenState.reduce(action: Action): ClickTrackListScreenState {
    return when (action) {
        is UpdateClickTrackList -> {
            ClickTrackListScreenState(action.data)
        }
        else -> this
    }
}
