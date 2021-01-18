package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.state.actions.PausePlay
import com.vsevolodganin.clicktrack.state.actions.StartPlay
import com.vsevolodganin.clicktrack.state.actions.StopPlay
import com.vsevolodganin.clicktrack.state.actions.UpdateCurrentlyPlaying
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ViewModelScoped
class PlayerEpic @Inject constructor(
    private val player: Player,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions
                .consumeEach { action ->
                    when (action) {
                        is StartPlay -> player.start(action.clickTrack, action.progress)
                        StopPlay -> player.stop()
                        PausePlay -> player.pause()
                    }
                },

            player.playbackState()
                .map(::UpdateCurrentlyPlaying)
        )
    }
}
