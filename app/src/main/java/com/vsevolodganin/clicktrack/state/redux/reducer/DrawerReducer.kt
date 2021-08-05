package com.vsevolodganin.clicktrack.state.redux.reducer

import com.vsevolodganin.clicktrack.state.redux.DrawerState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.action.CloseDrawer
import com.vsevolodganin.clicktrack.state.redux.action.OpenDrawer
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.frontScreen
import com.vsevolodganin.clicktrack.state.redux.frontScreenPosition

fun DrawerState.reduceDrawerState(action: Action, screens: List<Screen>): DrawerState {
    val isOpened = isOpened.reduceIsOpened(action)
    return copy(
        isOpened = isOpened.reduceIsOpened(action),
        gesturesEnabled = isOpened || screens.frontScreenPosition() == 0,
        selectedItem = screens.frontScreen()?.let { screen ->
            when (screen) {
                is Screen.Metronome -> DrawerState.SelectedItem.METRONOME
                is Screen.Training -> DrawerState.SelectedItem.TRAINING
                is Screen.Settings -> DrawerState.SelectedItem.SETTINGS
                is Screen.SoundLibrary -> DrawerState.SelectedItem.SOUND_LIBRARY
                is Screen.About -> DrawerState.SelectedItem.ABOUT
                Screen.ClickTrackList,
                is Screen.EditClickTrack,
                is Screen.PlayClickTrack -> null
            }
        },
    )
}

private fun Boolean.reduceIsOpened(action: Action): Boolean {
    return when (action) {
        is OpenDrawer -> true
        is CloseDrawer -> false
        else -> this
    }
}
