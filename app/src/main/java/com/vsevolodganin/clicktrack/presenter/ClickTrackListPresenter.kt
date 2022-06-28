package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.ui.model.ClickTrackListUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClickTrackListPresenter @Inject constructor(
    private val clickTraLiRepository: ClickTrackRepository,
) {
    fun uiScreens(): Flow<UiScreen.ClickTrackList> {
        return clickTraLiRepository.getAll()
            .map(::ClickTrackListUiState)
            .map(UiScreen::ClickTrackList)
    }
}
