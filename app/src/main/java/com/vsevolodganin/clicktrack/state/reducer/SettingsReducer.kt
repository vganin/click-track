package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.state.actions.SettingsAction
import com.vsevolodganin.clicktrack.theme.Theme

fun Screen.Settings.reduceSettings(action: Action): Screen.Settings {
    return Screen.Settings(
        state = state.reduce(action)
    )
}

private fun SettingsScreenState?.reduce(action: Action): SettingsScreenState? {
    return when (action) {
        is SettingsAction.SetScreenState -> action.state
        else -> this?.copy(
            theme = theme.reduce(action)
        )
    }
}

private fun Theme.reduce(action: Action): Theme {
    return when (action) {
        is SettingsAction.ChangeTheme -> action.value
        else -> this
    }
}
