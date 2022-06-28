package com.vsevolodganin.clicktrack.presenter

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.player.PlaybackState
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.redux.MetronomeState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.MetronomeUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MetronomePresenter @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val player: Player,
) {
    fun uiScreens(screens: Flow<Screen.Metronome>): Flow<UiScreen.Metronome> {
        return combine(
            screens.map { it.state },
            userPreferencesRepository.metronomeBpm.stateFlow,
            userPreferencesRepository.metronomePattern.stateFlow,
            player.playbackState(),
            ::uiState,
        ).map(UiScreen::Metronome)
    }

    private fun uiState(
        metronomeState: MetronomeState,
        bpm: BeatsPerMinute,
        pattern: NotePattern,
        playbackState: PlaybackState?,
    ): MetronomeUiState {
        val isPlaying = playbackState?.id == ClickTrackId.Builtin.Metronome

        return MetronomeUiState(
            bpm = bpm,
            pattern = pattern,
            isPlaying = isPlaying,
            progress = grabIf(isPlaying) { playbackState?.progress },
            areOptionsExpanded = metronomeState.areOptionsExpanded,
        )
    }
}
