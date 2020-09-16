package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.DataLoadedAction

fun Screen.ClickTrackList.reduceClickTrackListScreen(action: Action): Screen {
    return when (action) {
        is DataLoadedAction -> Screen.ClickTrackList(ClickTrackListScreenState(action.data))
        else -> this
    }
}
