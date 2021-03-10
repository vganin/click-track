package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transformLatest

@ViewModelScoped
class PlayerEpic @Inject constructor(
    private val player: Player,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions
                .consumeEach { action ->
                    when (action) {
                        is ClickTrackAction.StartPlay -> player.start(action.clickTrack, action.progress)
                        ClickTrackAction.StopPlay -> player.stop()
                        ClickTrackAction.PausePlay -> player.pause()
                    }
                },

            player.playbackState()
                .map(ClickTrackAction::UpdateCurrentlyPlaying),

            actions.filterIsInstance<ClickTrackAction.UpdateClickTrack>()
                .transformLatest { action ->
                    val playbackState = player.playbackState().firstOrNull()
                    if (playbackState?.clickTrack?.id == action.data.id) {
                        emit(ClickTrackAction.StartPlay(action.data))
                    }
                },
        )
    }
}
