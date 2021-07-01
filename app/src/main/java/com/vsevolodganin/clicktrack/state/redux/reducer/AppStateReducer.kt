package com.vsevolodganin.clicktrack.state.redux.reducer

import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.core.Action

fun AppState.reduce(action: Action): AppState {
    val backstack = backstack.reduce(action)
    return AppState(
        backstack = backstack,
    )
}
