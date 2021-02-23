package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.state.actions.SettingsActions
import com.vsevolodganin.clicktrack.theme.Theme

fun Screen.Settings.reduceSettings(action: Action): Screen.Settings {
    return Screen.Settings(
        state = state.reduce(action)
    )
}

private fun SettingsScreenState?.reduce(action: Action): SettingsScreenState? {
    return when (action) {
        is SettingsActions.SetScreenState -> action.state
        else -> this?.copy(
            theme = theme.reduce(action)
        )
    }
}

private fun Theme.reduce(action: Action): Theme {
    return when (action) {
        is SettingsActions.ChangeTheme -> action.value
        else -> this
    }
}
