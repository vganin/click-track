package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.theme.Theme

sealed interface SettingsAction : Action {

    class ChangeTheme(val value: Theme) : SettingsAction
}
