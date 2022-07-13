package com.vsevolodganin.clicktrack.redux.reducer

import com.vsevolodganin.clicktrack.redux.DrawerState
import com.vsevolodganin.clicktrack.redux.action.DrawerAction
import com.vsevolodganin.clicktrack.redux.core.Action

fun DrawerState.reduce(action: Action): DrawerState {
    val isOpened = isOpened.reduceIsOpened(action)
    return copy(
        isOpened = isOpened.reduceIsOpened(action),
        selectedItem = selectedItem.reduce(action),
    )
}

private fun Boolean.reduceIsOpened(action: Action): Boolean {
    return when (action) {
        is DrawerAction.Open -> true
        is DrawerAction.Close -> false
        else -> this
    }
}

private fun DrawerState.SelectedItem?.reduce(action: Action): DrawerState.SelectedItem? {
    return when (action) {
        is DrawerAction.SelectItem -> action.item
        else -> this
    }
}
