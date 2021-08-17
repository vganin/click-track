package com.vsevolodganin.clicktrack.state.redux.epic

import android.content.Context
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.model.metronomeClickTrack
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.state.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import com.vsevolodganin.clicktrack.utils.flow.consumeEachLatest
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take

@ViewModelScoped
class PlayerEpic @Inject constructor(
    private val player: Player,
    private val clickTrackRepository: ClickTrackRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            player.playbackState()
                .map { it?.id }
                .distinctUntilChanged()
                .consumeEachLatest(::drivePlayerByPlayableUpdates),

            actions.filterIsInstance<PlayerAction.StartPlayClickTrack>()
                .consumeEach { action ->
                    val clickTrack = clickTrackUpdates(action.id).take(1).single() ?: return@consumeEach
                    player.start(clickTrack, action.progress)
                },

            actions.filterIsInstance<PlayerAction.StartPlayPolyrhythm>()
                .consumeEach {
                    val polyrhythm = polyrhythmUpdates().take(1).single()
                    player.start(polyrhythm)
                },

            actions.filterIsInstance<PlayerAction.StopPlay>()
                .consumeEach {
                    player.stop()
                },

            actions.filterIsInstance<PlayerAction.PausePlay>()
                .consumeEach {
                    player.pause()
                },
        )
    }

    private suspend fun drivePlayerByPlayableUpdates(id: PlayableId?) {
        id ?: return

        when (id) {
            is ClickTrackId -> clickTrackUpdates(id)
                .collect { clickTrack ->
                    if (clickTrack != null) {
                        player.start(clickTrack)
                    } else {
                        player.stop()
                    }
                }
            TwoLayerPolyrhythmId -> polyrhythmUpdates()
                .collect { polyrhythm ->
                    player.start(polyrhythm)
                }
        }
    }

    private fun clickTrackUpdates(id: ClickTrackId): Flow<ClickTrackWithId?> {
        return when (id) {
            ClickTrackId.Builtin.Metronome -> metronomeClickTrackUpdates()
            is ClickTrackId.Builtin.ClickSoundsTest -> emptyFlow()
            is ClickTrackId.Database -> clickTrackRepository.getById(id)
        }
    }

    private fun metronomeClickTrackUpdates(): Flow<ClickTrackWithId> {
        return combine(
            userPreferencesRepository.metronomeBpm.flow,
            userPreferencesRepository.metronomePattern.flow,
        ) { bpm, pattern ->
            metronomeClickTrack(
                name = context.getString(R.string.metronome),
                bpm = bpm,
                pattern = pattern,
            )
        }
    }

    private fun polyrhythmUpdates(): Flow<TwoLayerPolyrhythm> {
        return userPreferencesRepository.polyrhythm.flow
    }
}
