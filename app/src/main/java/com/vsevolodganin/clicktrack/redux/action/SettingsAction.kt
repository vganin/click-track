package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.theme.Theme

sealed interface SettingsAction : Action {
    class ChangeTheme(val value: Theme) : SettingsAction
    class ChangeIgnoreAudioFocus(val value: Boolean) : SettingsAction
}
