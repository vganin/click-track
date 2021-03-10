package com.vsevolodganin.clicktrack.state.utils

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.frontScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

inline fun <reified S : Screen> Store<AppState>.onScreen(crossinline transform: suspend (value: S) -> Flow<Action>): Flow<Action> {
    return state
        .map { it.backstack.frontScreen() }
        .distinctUntilChangedBy { it?.javaClass }
        .flatMapLatest { screen ->
            if (screen is S) {
                transform(screen)
            } else {
                emptyFlow()
            }
        }
}
