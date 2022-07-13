package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.MetronomeState
import com.vsevolodganin.clicktrack.redux.PlayClickTrackState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.ScreenBackstack
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.toEditState
import com.vsevolodganin.clicktrack.utils.grabIf

fun ScreenBackstack.reduce(action: Action): ScreenBackstack {
    return reduceFrontScreen(action).reduceNavigation(action)
}

private fun ScreenBackstack.reduceFrontScreen(action: Action): ScreenBackstack {
    return copy(frontScreen = frontScreen.reduce(action))
}

private fun ScreenBackstack.reduceNavigation(action: Action): ScreenBackstack {
    return when (action) {
        is BackstackAction.Pop -> {
            if (restScreens.isEmpty()) {
                this
            } else {
                copy(
                    frontScreen = restScreens.last(),
                    restScreens = restScreens.dropLast(1),
                )
            }
        }
        is BackstackAction.ToClickTrackListScreen -> copy(
            frontScreen = Screen.ClickTrackList,
            restScreens = emptyList(),
        )
        is BackstackAction.ToClickTrackScreen -> copy(
            frontScreen = Screen.PlayClickTrack(
                state = PlayClickTrackState(
                    id = action.id
                )
            ),
            restScreens = listOf(
                Screen.ClickTrackList,
            ),
        )
        is BackstackAction.ToEditClickTrackScreen -> copy(
            frontScreen = Screen.EditClickTrack(
                state = action.clickTrack.toEditState(isInitialEdit = action.isInitialEdit)
            ),
            restScreens = listOfNotNull(
                Screen.ClickTrackList,
                grabIf(!action.isInitialEdit) {
                    Screen.PlayClickTrack(
                        state = PlayClickTrackState(
                            id = action.clickTrack.id
                        )
                    )
                },
            ),
        )
        BackstackAction.ToMetronomeScreen -> copy(
            frontScreen = Screen.Metronome(
                state = MetronomeState(
                    areOptionsExpanded = false
                )
            ),
            restScreens = listOf(
                Screen.ClickTrackList,
            ),
        )
        is BackstackAction.ToTrainingScreen -> copy(
            frontScreen = Screen.Training(
                state = action.state
            ),
            restScreens = listOf(
                Screen.ClickTrackList,
            ),
        )
        BackstackAction.ToSettingsScreen -> copy(
            frontScreen = Screen.Settings, restScreens = listOf(
                Screen.ClickTrackList,
            )
        )
        BackstackAction.ToSoundLibraryScreen -> copy(
            frontScreen = Screen.SoundLibrary, restScreens = listOf(
                Screen.ClickTrackList,
            )
        )
        BackstackAction.ToAboutScreen -> copy(
            frontScreen = Screen.About, restScreens = listOf(
                Screen.ClickTrackList,
            )
        )
        BackstackAction.ToPolyrhythms -> copy(
            frontScreen = Screen.Polyrhythms, restScreens = listOf(
                Screen.ClickTrackList,
            )
        )
        else -> this
    }
}
