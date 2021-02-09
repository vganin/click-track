package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.MetronomeActions
import com.vsevolodganin.clicktrack.state.actions.StartPlay
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import javax.inject.Inject
import kotlin.time.milliseconds

@ViewModelScoped
class MetronomeEpic @Inject constructor(
    private val store: Store<AppState>,
    private val userPreferencesRepository: UserPreferencesRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .mapNotNull { screen ->
                    if (screen is Screen.Metronome) {
                        val currentlyPlayingMetronome = currentlyPlayingMetronome()
                        MetronomeScreenState(
                            bpm = userPreferencesRepository.metronomeBpm,
                            progress = currentlyPlayingMetronome?.progress,
                            isPlaying = currentlyPlayingMetronome != null
                        )
                    } else {
                        null
                    }
                }
                .map(MetronomeActions::UpdateMetronomeState),

            actions.filterIsInstance<MetronomeActions.ChangeBpm>()
                .debounce(100.milliseconds)
                .mapNotNull { action ->
                    currentlyPlayingMetronome()?.clickTrack?.run {
                        copy(
                            value = value.copy(
                                cues = value.cues.map {
                                    it.copy(bpm = action.bpm)
                                }
                            )
                        )
                    }?.let { updatedClickTrack ->
                        StartPlay(clickTrack = updatedClickTrack, progress = action.startProgress)
                    }
                },

            actions
                .filterIsInstance<MetronomeActions.ChangeBpm>()
                .debounce(100.milliseconds)
                .consumeEach { action ->
                    userPreferencesRepository.metronomeBpm = action.bpm
                }
        )
    }

    private fun currentlyPlayingMetronome(): PlaybackState? {
        return store.state.value.currentlyPlaying?.takeIf { it.clickTrack.id == MetronomeId }
    }
}
