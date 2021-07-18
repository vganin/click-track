package com.vsevolodganin.clicktrack.state.redux.reducer

import com.vsevolodganin.clicktrack.state.redux.MetronomeState
import com.vsevolodganin.clicktrack.state.redux.PlayClickTrackState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.ScreenBackstack
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.pop
import com.vsevolodganin.clicktrack.state.redux.pushOrIgnore
import com.vsevolodganin.clicktrack.state.redux.pushOrReplace
import com.vsevolodganin.clicktrack.state.redux.replaceCurrentScreen
import com.vsevolodganin.clicktrack.state.redux.toEditState

fun ScreenBackstack.reduce(action: Action): ScreenBackstack {
    return when (action) {
        is NavigationAction -> reduce(action)
        else -> {
            val screens = screens.replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }
            ScreenBackstack(
                screens = screens,
                drawerState = drawerState.reduceDrawerState(action, screens)
            )
        }
    }
}

private fun ScreenBackstack.reduce(action: NavigationAction): ScreenBackstack {
    return when (action) {
        NavigationAction.Back -> {
            if (drawerState.isOpened) {
                copy(drawerState = drawerState.copy(isOpened = false))
            } else {
                copy(screens = screens.pop())
            }
        }
        is NavigationAction.ToClickTrackListScreen -> copy(screens = screens.pushOrReplace(
            Screen.ClickTrackList
        ))
        is NavigationAction.ToClickTrackScreen -> copy(screens = screens.pushOrReplace {
            Screen.PlayClickTrack(
                state = PlayClickTrackState(
                    id = action.id
                )
            )
        })
        is NavigationAction.ToEditClickTrackScreen -> copy(screens = screens.pushOrReplace(
            Screen.EditClickTrack(
                state = action.clickTrack.toEditState()
            )
        ))
        NavigationAction.ToMetronomeScreen -> copy(screens = screens.pushOrIgnore {
            Screen.Metronome(
                state = MetronomeState(
                    areOptionsExpanded = false
                )
            )
        })
        NavigationAction.ToSettingsScreen -> copy(screens = screens.pushOrReplace {
            Screen.Settings
        })
        NavigationAction.ToSoundLibraryScreen -> copy(screens = screens.pushOrReplace {
            Screen.SoundLibrary
        })
    }
}
