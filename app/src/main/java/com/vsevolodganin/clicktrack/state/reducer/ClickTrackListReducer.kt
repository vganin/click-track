package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.ClickTrackListAction

fun Screen.ClickTrackList.reduceClickTrackListScreen(action: Action): Screen {
    return Screen.ClickTrackList(
        state = state.reduce(action)
    )
}

private fun ClickTrackListScreenState.reduce(action: Action): ClickTrackListScreenState {
    return when (action) {
        is ClickTrackListAction.SetData -> ClickTrackListScreenState(action.data)
        is ClickTrackAction.RemoveClickTrack -> copy(items = items.filterNot { it.id == action.id })
        else -> this
    }
}
