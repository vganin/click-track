package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.player.Player
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.StartPlay
import net.ganin.vsevolod.clicktrack.state.actions.StopPlay
import net.ganin.vsevolod.clicktrack.state.actions.UpdateCurrentlyPlaying
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import javax.inject.Inject

@ViewModelScoped
class PlayerEpic @Inject constructor(
    private val player: Player
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions
                .consumeEach { action ->
                    when (action) {
                        is StartPlay -> player.play(action.clickTrack)
                        StopPlay -> player.stop()
                    }
                },

            player.playbackState()
                .map(::UpdateCurrentlyPlaying)
        )
    }
}
