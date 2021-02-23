package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.theme.Theme

sealed class SettingsActions : Action {

    class SetScreenState(val state: SettingsScreenState) : SettingsActions()

    class ChangeTheme(val value: Theme) : SettingsActions()
}
