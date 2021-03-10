package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.theme.Theme

sealed class SettingsAction : Action {

    class SetScreenState(val state: SettingsScreenState) : SettingsAction()

    class ChangeTheme(val value: Theme) : SettingsAction()
}
