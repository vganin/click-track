package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.DrawerScreenState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.ScreenBackstack
import com.vsevolodganin.clicktrack.state.actions.CloseDrawer
import com.vsevolodganin.clicktrack.state.actions.OpenDrawer
import com.vsevolodganin.clicktrack.state.actions.UpdateCurrentlyPlaying
import com.vsevolodganin.clicktrack.state.frontScreen

fun AppState.reduce(action: Action): AppState {
    val currentlyPlaying = currentlyPlaying.reduce(action)
    val backstack = backstack.reduce(action, currentlyPlaying)
    return AppState(
        backstack = backstack,
        drawerState = drawerState.reduce(action, backstack),
        currentlyPlaying = currentlyPlaying,
    )
}

private fun DrawerScreenState.reduce(action: Action, backstack: ScreenBackstack): DrawerScreenState {
    val currentScreen = backstack.frontScreen()
    val isOpened = when (action) {
        is OpenDrawer -> true
        is CloseDrawer -> false
        else -> isOpened
    }
    return DrawerScreenState(isOpened = isOpened, currentScreen = currentScreen)
}

private fun PlaybackState?.reduce(action: Action): PlaybackState? {
    return when (action) {
        is UpdateCurrentlyPlaying -> action.playbackState
        else -> this
    }
}
