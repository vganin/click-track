package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.state.redux.DrawerState
import com.vsevolodganin.clicktrack.ui.model.DrawerUiState
import dagger.Reusable
import javax.inject.Inject

@Reusable
class DrawerPresenter @Inject constructor() {

    fun uiState(drawerState: DrawerState): DrawerUiState {
        return DrawerUiState(
            isOpened = drawerState.isOpened,
            gesturesEnabled = drawerState.gesturesEnabled,
            selectedItem = drawerState.selectedItem,
            displayVersion = BuildConfig.DISPLAY_VERSION,
        )
    }
}
