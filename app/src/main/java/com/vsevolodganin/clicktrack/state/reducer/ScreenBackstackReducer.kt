package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.PlayClickTrackScreenState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.ScreenBackstack
import com.vsevolodganin.clicktrack.state.actions.NavigateBack
import com.vsevolodganin.clicktrack.state.actions.NavigateToClickTrackListScreen
import com.vsevolodganin.clicktrack.state.actions.NavigateToClickTrackScreen
import com.vsevolodganin.clicktrack.state.actions.NavigateToEditClickTrackScreen
import com.vsevolodganin.clicktrack.state.actions.NavigateToMetronomeScreen
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.pop
import com.vsevolodganin.clicktrack.state.pushOrIgnore
import com.vsevolodganin.clicktrack.state.pushOrReplace
import com.vsevolodganin.clicktrack.state.replaceCurrentScreen

fun ScreenBackstack.reduce(action: Action, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action, currentlyPlaying)
        else -> replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }
    }
}

private fun ScreenBackstack.reduce(action: NavigationAction, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        NavigateBack -> pop()
        is NavigateToClickTrackListScreen -> pushOrReplace(
            Screen.ClickTrackList(
                state = ClickTrackListScreenState(
                    items = action.clickTrack
                )
            )
        )
        is NavigateToClickTrackScreen -> pushOrReplace {
            val clickTrack = action.clickTrack
            val progress = currentlyPlaying?.takeIf { it.clickTrack.id == clickTrack.id }?.progress
            Screen.PlayClickTrack(
                state = PlayClickTrackScreenState(
                    clickTrack = clickTrack,
                    progress = progress,
                    isPlaying = progress != null
                )
            )
        }
        is NavigateToEditClickTrackScreen -> pushOrReplace(
            Screen.EditClickTrack(
                state = EditClickTrackScreenState(
                    clickTrack = action.clickTrack,
                    isErrorInName = false,
                )
            )
        )
        NavigateToMetronomeScreen -> pushOrIgnore(
            Screen.Metronome(
                state = null
            )
        )
    }
}
