package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.MetronomeState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.action.MetronomeAction
import com.vsevolodganin.clicktrack.redux.core.Action

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return copy(
        state = state.reduce(action)
    )
}

private fun MetronomeState.reduce(action: Action): MetronomeState {
    return when (action) {
        is MetronomeAction.ToggleOptions -> copy(areOptionsExpanded = !areOptionsExpanded)
        is MetronomeAction.OpenOptions -> copy(areOptionsExpanded = true)
        is MetronomeAction.CloseOptions -> copy(areOptionsExpanded = false)
        else -> this
    }
}
