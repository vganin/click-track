package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.state.frontScreenPosition

fun DrawerScreenState.reduceDrawerState(action: Action, screens: List<Screen>): DrawerScreenState {
    val isOpened = isOpened.reduceIsOpened(action)
    return copy(
        isOpened = isOpened.reduceIsOpened(action),
        gesturesEnabled = isOpened || screens.frontScreenPosition() == 0,
        selectedItem = screens.frontScreen()?.let { screen ->
            when (screen) {
                is Screen.Metronome -> DrawerScreenState.SelectedItem.METRONOME
                is Screen.Settings -> DrawerScreenState.SelectedItem.SETTINGS
                is Screen.SoundLibrary -> DrawerScreenState.SelectedItem.SOUND_LIBRARY
                else -> null
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
