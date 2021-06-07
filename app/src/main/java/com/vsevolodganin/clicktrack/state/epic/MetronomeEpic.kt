package com.vsevolodganin.clicktrack.state.epic

import android.content.Context
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.meter.BpmMeter
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.metronomeClickTrack
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import com.vsevolodganin.clicktrack.utils.optionalCast
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

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
                        val currentlyPlayingMetronome = store.state.value.currentlyPlaying
                            ?.takeIf { it.clickTrack.id == ClickTrackId.Builtin.METRONOME }
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
                    bpmMeter.calculateBpm()?.let(MetronomeAction::SetBpm)
                },

            store.state
                .mapNotNull { it.backstack.screens.frontScreen().optionalCast<Screen.Metronome>()?.state }
                .distinctUntilChangedBy(MetronomeScreenState::clickTrack)
                .transform { metronomeState ->
                    if (metronomeState.isPlaying) {
                        emit(ClickTrackAction.StartPlay(clickTrack = metronomeState.clickTrack))
                    }
                },

            store.state
                .mapNotNull { it.backstack.screens.frontScreen().optionalCast<Screen.Metronome>()?.state }
                .map { it.clickTrack.value.cues.first().bpm }
                .distinctUntilChanged()
                .debounce(Duration.milliseconds(100))
                .consumeEach { bpm ->
                    userPreferencesRepository.metronomeBpm = bpm
                },

            store.state
                .mapNotNull { it.backstack.screens.frontScreen().optionalCast<Screen.Metronome>()?.state }
                .map { it.clickTrack.value.cues.first().pattern }
                .distinctUntilChanged()
                .debounce(Duration.milliseconds(100))
                .consumeEach { pattern ->
                    userPreferencesRepository.metronomePattern = pattern
                },
        )
    }
}
