package net.ganin.vsevolod.clicktrack.state.reducer

import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.state.AppState

fun AppState.reduce(action: Action): AppState {
    return AppState(backstack.reduce(action))
}
