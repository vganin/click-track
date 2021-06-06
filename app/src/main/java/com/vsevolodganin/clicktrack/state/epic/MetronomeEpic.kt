package com.vsevolodganin.clicktrack.state.epic

import android.content.Context
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.meter.BpmMeter
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.model.metronomeClickTrack
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

@ViewModelScoped
class MetronomeEpic @Inject constructor(
    private val store: Store<AppState>,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val bpmMeter: BpmMeter,
    @ApplicationContext private val context: Context,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            // TODO: Use `onScreen`
            store.state
                .map { it.backstack.screens.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .mapNotNull { screen ->
                    if (screen is Screen.Metronome) {
                        val currentlyPlayingMetronome = currentlyPlayingMetronome()
                        val areOptionsExpanded = screen.state?.areOptionsExpanded ?: false
                        MetronomeScreenState(
                            clickTrack = metronomeClickTrack(
                                name = context.getString(R.string.metronome),
                                bpm = userPreferencesRepository.metronomeBpm,
                                pattern = userPreferencesRepository.metronomePattern,
                            ),
                            progress = currentlyPlayingMetronome?.progress,
                            isPlaying = currentlyPlayingMetronome != null,
                            areOptionsExpanded = areOptionsExpanded,
                        )
                    } else {
                        null
                    }
                }
                .map(MetronomeAction::SetScreenState),

            actions.filterIsInstance<MetronomeAction.BpmMeterTap>()
                .mapNotNull {
                    bpmMeter.addTap()
                    bpmMeter.calculateBpm()?.let(MetronomeAction::ChangeBpm)
                },

            actions.filterIsInstance<MetronomeAction.ChangeBpm>()
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
                        ClickTrackAction.StartPlay(clickTrack = updatedClickTrack)
                    }
                },

            actions
                .filterIsInstance<MetronomeAction.ChangeBpm>()
                .debounce(Duration.milliseconds(100))
                .consumeEach { action ->
                    userPreferencesRepository.metronomeBpm = action.bpm
                },

            actions.filterIsInstance<MetronomeAction.ChangePattern>()
                .mapNotNull { action ->
                    currentlyPlayingMetronome()?.clickTrack?.run {
                        copy(
                            value = value.copy(
                                cues = value.cues.map {
                                    it.copy(pattern = action.pattern)
                                }
                            )
                        )
                    }?.let { updatedClickTrack ->
                        ClickTrackAction.StartPlay(clickTrack = updatedClickTrack)
                    }
                },

            actions
                .filterIsInstance<MetronomeAction.ChangePattern>()
                .debounce(Duration.milliseconds(100))
                .consumeEach { action ->
                    userPreferencesRepository.metronomePattern = action.pattern
                }
        )
    }

    private fun currentlyPlayingMetronome(): PlaybackState? {
        return store.state.value.currentlyPlaying?.takeIf { it.clickTrack.id == MetronomeId }
    }
}
