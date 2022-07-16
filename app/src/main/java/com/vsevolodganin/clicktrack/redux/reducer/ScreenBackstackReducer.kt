package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.MetronomeState
import com.vsevolodganin.clicktrack.redux.PlayClickTrackState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.ScreenBackstack
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.pop
import com.vsevolodganin.clicktrack.redux.push
import com.vsevolodganin.clicktrack.redux.resetTo
import com.vsevolodganin.clicktrack.redux.toEditState

fun ScreenBackstack.reduce(action: Action): ScreenBackstack {
    return reduceFrontScreen(action).reduceNavigation(action)
}

private fun ScreenBackstack.reduceFrontScreen(action: Action): ScreenBackstack {
    return copy(frontScreen = frontScreen.reduce(action))
}

private fun ScreenBackstack.reduceNavigation(action: Action): ScreenBackstack {
    return when (action) {
        is BackstackAction.Pop -> pop()
        is BackstackAction.ToClickTrackListScreen -> ScreenBackstack(
            frontScreen = Screen.ClickTrackList,
            restScreens = emptyList(),
        )
        is BackstackAction.ToClickTrackScreen -> {
            pop { it is Screen.EditClickTrack }
                .pop { it is Screen.PlayClickTrack }
                .push(Screen.PlayClickTrack(PlayClickTrackState(action.id)))
        }
        is BackstackAction.ToEditClickTrackScreen -> {
            pop { it is Screen.EditClickTrack }
                .push(Screen.EditClickTrack(action.clickTrack.toEditState(action.isInitialEdit)))
        }
        BackstackAction.ToMetronomeScreen -> resetTo(Screen.Metronome(MetronomeState(areOptionsExpanded = false)))
        is BackstackAction.ToTrainingScreen -> resetTo(Screen.Training(action.state))
        BackstackAction.ToSettingsScreen -> resetTo(Screen.Settings)
        BackstackAction.ToSoundLibraryScreen -> resetTo(Screen.SoundLibrary)
        BackstackAction.ToAboutScreen -> resetTo(Screen.About)
        BackstackAction.ToPolyrhythms -> resetTo(Screen.Polyrhythms)
        else -> this
    }
}
