package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.ClickTrackListScreenState
import net.ganin.vsevolod.clicktrack.state.EditClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.PlayClickTrackScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.ScreenBackstack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackListScreen
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.NavigationAction
import net.ganin.vsevolod.clicktrack.state.pop
import net.ganin.vsevolod.clicktrack.state.push
import net.ganin.vsevolod.clicktrack.state.replaceCurrentScreen

fun ScreenBackstack.reduce(action: Action, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action, currentlyPlaying)
        else -> replaceCurrentScreen { currentScreen -> currentScreen.reduce(action, currentlyPlaying) }
    }
}

private fun ScreenBackstack.reduce(action: NavigationAction, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        NavigateBack -> pop()
        is NavigateToClickTrackListScreen -> push(
            Screen.ClickTrackList(
                state = ClickTrackListScreenState(
                    items = action.clickTrack
                )
            )
        )
        is NavigateToClickTrackScreen -> push(
            Screen.PlayClickTrack(
                state = PlayClickTrackScreenState(
                    clickTrack = action.clickTrack,
                    currentlyPlaying = currentlyPlaying
                )
            )
        )
        is NavigateToEditClickTrackScreen -> push(
            Screen.EditClickTrack(
                state = EditClickTrackScreenState(
                    clickTrack = action.clickTrack,
                    isErrorInName = false,
                )
            )
        )
    }
}
