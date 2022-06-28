package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.MetronomeState
import com.vsevolodganin.clicktrack.redux.PlayClickTrackState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.ScreenBackstack
import com.vsevolodganin.clicktrack.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.pop
import com.vsevolodganin.clicktrack.redux.pushOrIgnore
import com.vsevolodganin.clicktrack.redux.pushOrReplace
import com.vsevolodganin.clicktrack.redux.replaceCurrentScreen
import com.vsevolodganin.clicktrack.redux.toEditState

fun ScreenBackstack.reduce(action: Action): ScreenBackstack {
    var screens = if (action is NavigationAction) {
        reduce(action).screens
    } else {
        screens
    }

    screens = screens.replaceCurrentScreen { currentScreen -> currentScreen.reduce(action) }

    return ScreenBackstack(
        screens = screens,
        drawerState = drawerState.reduceDrawerState(action, screens)
    )
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
        is NavigationAction.ToTrainingScreen -> copy(screens = screens.pushOrIgnore {
            Screen.Training(
                state = action.state
            )
        })
        NavigationAction.ToSettingsScreen -> copy(screens = screens.pushOrReplace {
            Screen.Settings
        })
        NavigationAction.ToSoundLibraryScreen -> copy(screens = screens.pushOrReplace {
            Screen.SoundLibrary
        })
        NavigationAction.ToAboutScreen -> copy(screens = screens.pushOrReplace {
            Screen.About
        })
        NavigationAction.ToPolyrhythms -> copy(screens = screens.pushOrReplace {
            Screen.Polyrhythms
        })
    }
}
