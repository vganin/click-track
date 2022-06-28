package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.ui.model.AboutUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AboutPresenter @Inject constructor() {

    fun uiScreens(): Flow<UiScreen.About> {
        return flowOf(
            UiScreen.About(
                AboutUiState(
                    displayVersion = BuildConfig.DISPLAY_VERSION,
                )
            )
        )
    }
}
