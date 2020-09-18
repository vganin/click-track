package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.ClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.ScreenBackstack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackListScreen
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.NavigationAction
import net.ganin.vsevolod.clicktrack.state.pop
import net.ganin.vsevolod.clicktrack.state.push
import net.ganin.vsevolod.clicktrack.state.replaceCurrentScreen

fun ScreenBackstack.reduce(action: Action): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action)
        else -> replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }
    }
}

private fun ScreenBackstack.reduce(action: NavigationAction): ScreenBackstack {
    return when (action) {
        NavigateBack -> pop()
        is NavigateToClickTrackListScreen -> push(
            Screen.ClickTrackList(
                state = ClickTrackListScreenState(
                    items = action.data
                )
            )
        )
        is NavigateToClickTrackScreen -> push(
            Screen.ClickTrack(
                state = ClickTrackScreenState(
                    clickTrack = action.data,
                    isPlaying = false,
                    playbackStamp = null,
                )
            )
        )
    }
}
