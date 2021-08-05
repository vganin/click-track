package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.state.redux.DrawerState

data class DrawerUiState(
    val isOpened: Boolean,
    val gesturesEnabled: Boolean,
    val selectedItem: DrawerState.SelectedItem?,
)
