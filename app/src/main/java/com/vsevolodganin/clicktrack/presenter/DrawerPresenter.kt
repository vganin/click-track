package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.redux.DrawerState
import com.vsevolodganin.clicktrack.ui.model.DrawerUiState
import javax.inject.Inject

class DrawerPresenter @Inject constructor() {

    fun uiState(drawerState: DrawerState): DrawerUiState {
        return DrawerUiState(
            isOpened = drawerState.isOpened,
            gesturesEnabled = drawerState.gesturesEnabled,
            selectedItem = drawerState.selectedItem,
        )
    }
}
