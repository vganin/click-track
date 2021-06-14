package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.screen.ClickTrackListScreenState
import com.vsevolodganin.clicktrack.state.screen.EditClickTrackScreenState
import com.vsevolodganin.clicktrack.state.screen.PlayClickTrackScreenState
import com.vsevolodganin.clicktrack.state.screen.Screen
import com.vsevolodganin.clicktrack.state.screen.ScreenBackstack
import com.vsevolodganin.clicktrack.state.screen.pop
import com.vsevolodganin.clicktrack.state.screen.pushOrIgnore
import com.vsevolodganin.clicktrack.state.screen.pushOrReplace
import com.vsevolodganin.clicktrack.state.screen.replaceCurrentScreen

fun ScreenBackstack.reduce(action: Action, currentlyPlaying: PlaybackState?): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action, currentlyPlaying)
        else -> {
            val screens = screens.replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }
            ScreenBackstack(
                screens = screens,
                drawerState = drawerState.reduceDrawerState(action, screens)
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
                copy(screens = screens.pop())
            }
        }
        is NavigationAction.ToClickTrackListScreen -> copy(screens = screens.pushOrReplace(
            Screen.ClickTrackList(
                state = ClickTrackListScreenState(
                    items = action.clickTrack
                )
            )
        ))
        is NavigationAction.ToClickTrackScreen -> copy(screens = screens.pushOrReplace {
            val clickTrack = action.clickTrack
            val progress = currentlyPlaying?.takeIf { it.clickTrack.id == clickTrack.id }?.progress
            Screen.PlayClickTrack(
                state = PlayClickTrackScreenState(
                    clickTrack = clickTrack,
                    progress = progress,
                    isPlaying = progress != null
                )
            )
        })
        is NavigationAction.ToEditClickTrackScreen -> copy(screens = screens.pushOrReplace(
            Screen.EditClickTrack(
                state = EditClickTrackScreenState(
                    clickTrack = action.clickTrack,
                    isErrorInName = false,
                )
            )
        ))
        NavigationAction.ToMetronomeScreen -> copy(screens = screens.pushOrIgnore {
            Screen.Metronome(state = null)
        })
        NavigationAction.ToSettingsScreen -> copy(screens = screens.pushOrReplace {
            Screen.Settings(state = null)
        })
        NavigationAction.ToSoundLibraryScreen -> copy(screens = screens.pushOrReplace {
            Screen.SoundLibrary(state = null)
        })
    }
}
