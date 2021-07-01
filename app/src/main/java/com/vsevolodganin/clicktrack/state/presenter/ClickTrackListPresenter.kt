package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.ui.model.ClickTrackListUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Reusable
class ClickTrackListPresenter @Inject constructor(
    private val clickTraLiRepository: ClickTrackRepository,
) {
    fun uiScreens(): Flow<UiScreen.ClickTrackList> {
        return clickTraLiRepository.getAll()
            .map(::ClickTrackListUiState)
            .map(UiScreen::ClickTrackList)
    }
}
