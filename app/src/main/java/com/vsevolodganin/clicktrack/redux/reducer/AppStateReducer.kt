package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.core.Action

fun AppState.reduce(action: Action): AppState {
    val backstack = backstack.reduce(action)
    return AppState(
        backstack = backstack,
    )
}
