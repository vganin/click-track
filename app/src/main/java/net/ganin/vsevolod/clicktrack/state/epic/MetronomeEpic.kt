package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.model.MetronomeId
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.MetronomeScreenState
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.MetronomeActions
import net.ganin.vsevolod.clicktrack.state.actions.StartPlay
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.storage.UserPreferencesRepository
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import javax.inject.Inject

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
                .mapNotNull { action ->
                    currentlyPlayingMetronome()?.clickTrack?.run {
                        copy(
                            value = value.copy(
                                cues = value.cues.map {
                                    it.copy(cue = it.cue.copy(bpm = action.bpm))
                                }
                            )
                        )
                    }?.let { updatedClickTrack ->
                        StartPlay(
                            clickTrack = updatedClickTrack,
                            progress = action.progress,
                        )
                    }
                },

            actions
                .filterIsInstance<MetronomeActions.ChangeBpm>()
                .consumeEach { action ->
                    userPreferencesRepository.metronomeBpm = action.bpm
                }
        )
    }

    private fun currentlyPlayingMetronome(): PlaybackState? {
        return store.state.value.currentlyPlaying?.takeIf { it.clickTrack.id == MetronomeId }
    }
}
