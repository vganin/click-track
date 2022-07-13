package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.core.Action

fun AppState.reduce(action: Action): AppState {
    return AppState(
        backstack = backstack.reduce(action),
        drawerState = drawerState.reduce(action)
    )
}
