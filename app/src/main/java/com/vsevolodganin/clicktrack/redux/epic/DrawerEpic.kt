package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.DrawerState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.action.DrawerAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@ActivityScoped
class DrawerEpic @Inject constructor(
    private val store: Store<AppState>,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return store.state
            .mapNotNull {
                when (it.backstack.frontScreen) {
                    is Screen.Metronome -> DrawerState.SelectedItem.METRONOME
                    is Screen.Training -> DrawerState.SelectedItem.TRAINING
                    is Screen.Settings -> DrawerState.SelectedItem.SETTINGS
                    is Screen.SoundLibrary -> DrawerState.SelectedItem.SOUND_LIBRARY
                    is Screen.About -> DrawerState.SelectedItem.ABOUT
                    is Screen.Polyrhythms -> DrawerState.SelectedItem.POLYRHYTHMS
                    Screen.ClickTrackList,
                    is Screen.EditClickTrack,
                    is Screen.PlayClickTrack -> null
                }.let(DrawerAction::SelectItem)
            }
    }
}
