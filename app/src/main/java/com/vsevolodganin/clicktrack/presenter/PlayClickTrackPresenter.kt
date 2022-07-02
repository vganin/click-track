package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.player.PlaybackState
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.PlayClickTrackUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayClickTrackPresenter @Inject constructor(
    private val clickTrackRepository: ClickTrackRepository,
    private val player: Player,
    private val userPreferences: UserPreferencesRepository,
) {
    fun uiScreens(screens: Flow<Screen.PlayClickTrack>): Flow<UiScreen.PlayClickTrack> {
        return combine(
            screens.map { it.state }.map { it.id }.flatMapLatest(clickTrackRepository::getById),
            userPreferences.playTrackingMode.stateFlow,
            player.playbackState(),
            ::uiState,
        )
            .filterNotNull()
            .map(UiScreen::PlayClickTrack)
    }

    private fun uiState(
        clickTrack: ClickTrackWithDatabaseId?,
        playTrackingMode: Boolean,
        playbackState: PlaybackState?,
    ): PlayClickTrackUiState? {
        clickTrack ?: return null

        val isPlaying = playbackState?.id == clickTrack.id

        return PlayClickTrackUiState(
            clickTrack = clickTrack,
            isPlaying = isPlaying,
            playProgress = grabIf(isPlaying) { playbackState?.progress },
            playTrackingMode = playTrackingMode,
        )
    }
}