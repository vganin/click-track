package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackListDataLoadedAction

fun Screen.ClickTrackList.reduceClickTrackListScreen(action: Action): Screen {
    return when (action) {
        is ClickTrackListDataLoadedAction -> Screen.ClickTrackList(ClickTrackListScreenState(action.data))
        else -> this
    }
}
