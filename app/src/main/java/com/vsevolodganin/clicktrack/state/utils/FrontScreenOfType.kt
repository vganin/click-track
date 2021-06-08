package com.vsevolodganin.clicktrack.state.utils

import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.utils.optionalCast

inline fun <reified S : Screen> Store<AppState>.frontScreenOfType(): S? {
    return state.value.frontScreenOfType()
}

inline fun <reified S : Screen> AppState.frontScreenOfType(): S? {
    return backstack.screens.frontScreen().optionalCast<S>()
}
