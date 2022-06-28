package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.player.PlaybackState
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.PolyrhythmsUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PolyrhythmsPresenter @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val player: Player,
) {

    fun uiScreens(): Flow<UiScreen.Polyrhythms> {
        return combine(
            userPreferencesRepository.polyrhythm.stateFlow,
            player.playbackState(),
            ::uiState,
        )
            .filterNotNull()
            .map(UiScreen::Polyrhythms)
    }

    private fun uiState(twoLayerPolyrhythm: TwoLayerPolyrhythm, playbackState: PlaybackState?): PolyrhythmsUiState {
        val isPlaying = playbackState?.id == TwoLayerPolyrhythmId

        return PolyrhythmsUiState(
            twoLayerPolyrhythm = twoLayerPolyrhythm,
            isPlaying = isPlaying,
            playableProgress = grabIf(isPlaying) { playbackState?.progress }
        )
    }
}
