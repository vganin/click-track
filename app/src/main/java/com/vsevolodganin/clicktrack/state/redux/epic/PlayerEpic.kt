package com.vsevolodganin.clicktrack.state.redux.epic

import android.content.Context
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.metronomeClickTrack
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.state.logic.ClickTrackValidator
import com.vsevolodganin.clicktrack.state.redux.action.PlayerAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import com.vsevolodganin.clicktrack.utils.flow.ignoreElements
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@ViewModelScoped
class PlayerEpic @Inject constructor(
    private val player: Player,
    private val clickTrackRepository: ClickTrackRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clickTrackValidator: ClickTrackValidator,
    @ApplicationContext val context: Context,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<PlayerAction.StartPlay>()
                .flatMapLatest { action ->
                    clickTrackUpdates(action.id)
                        .onEach { clickTrack ->
                            if (clickTrack != null) {
                                player.start(clickTrack, action.progress)
                            } else {
                                player.stop()
                            }
                        }
                }
                .ignoreElements(),

            // TODO: Subscribe to click track update upon app enter too

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

    private fun clickTrackUpdates(id: ClickTrackId): Flow<ClickTrackWithId?> {
        val clickTrackUpdates = when (id) {
            ClickTrackId.Builtin.METRONOME -> metronomeClickTrackUpdates()
            is ClickTrackId.Database -> clickTrackRepository.getById(id)
        }

        return clickTrackUpdates
//            .takeUntilSignal(
//                player.playbackState()
//                    .filter { it == null || it.clickTrack.id != id }
//            )
    }

    private fun metronomeClickTrackUpdates(): Flow<ClickTrackWithId> {
        return combine(
            userPreferencesRepository.metronomeBpmFlow.map(clickTrackValidator::limitBpm),
            userPreferencesRepository.metronomePatternFlow,
        ) { bpm, pattern ->
            metronomeClickTrack(
                name = context.getString(R.string.metronome),
                bpm = bpm,
                pattern = pattern,
            )
        }
    }
}
