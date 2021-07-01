package com.vsevolodganin.clicktrack.state.redux.reducer

import com.vsevolodganin.clicktrack.state.redux.MetronomeState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction
import com.vsevolodganin.clicktrack.state.redux.core.Action

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return Screen.Metronome(
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
