package com.vsevolodganin.clicktrack.ui.model

data class AppUiState(
    val screen: UiScreen,
    val screenPosition: Int,
    val drawerState: DrawerUiState,
)
