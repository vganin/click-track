package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.PlayClickTrackScreenState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.ScreenBackstack
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.state.pop
import com.vsevolodganin.clicktrack.state.pushOrIgnore
import com.vsevolodganin.clicktrack.state.pushOrReplace
import com.vsevolodganin.clicktrack.state.replaceCurrentScreen

fun ScreenBackstack.reduce(action: Action, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action, currentlyPlaying)
        else -> {
            replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }.copy(
                drawerState = drawerState.reduce(action)
            )
        }
    }
}

private fun ScreenBackstack.reduce(action: NavigationAction, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        NavigationAction.Back -> {
            if (drawerState.isOpened) {
                copy(drawerState = drawerState.copy(isOpened = false))
            } else {
                pop()
            }
        }
        is NavigationAction.ToClickTrackListScreen -> pushOrReplace(
            Screen.ClickTrackList(
                state = ClickTrackListScreenState(
                    items = action.clickTrack
                )
            )
        )
        is NavigationAction.ToClickTrackScreen -> pushOrReplace {
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
        is NavigationAction.ToEditClickTrackScreen -> pushOrReplace(
            Screen.EditClickTrack(
                state = EditClickTrackScreenState(
                    clickTrack = action.clickTrack,
                    isErrorInName = false,
                )
            )
        )
        NavigationAction.ToMetronomeScreen -> pushOrIgnore(
            Screen.Metronome(state = null)
        )
        NavigationAction.ToSettingsScreen -> pushOrReplace {
            Screen.Settings(state = null)
        }
        NavigationAction.ToSoundLibraryScreen -> pushOrReplace {
            Screen.SoundLibrary(state = null)
        }
    }
}

private fun DrawerScreenState.reduce(action: Action): DrawerScreenState {
    val isOpened = when (action) {
        is OpenDrawer -> true
        is CloseDrawer -> false
        else -> isOpened
    }
    return DrawerScreenState(isOpened = isOpened)
}
