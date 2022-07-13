package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.DrawerState
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface DrawerAction : Action {
    object Open : DrawerAction
    object Close : DrawerAction
    class SelectItem(val item: DrawerState.SelectedItem?) : DrawerAction
}
