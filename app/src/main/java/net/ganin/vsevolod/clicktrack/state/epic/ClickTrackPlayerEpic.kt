package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import net.ganin.vsevolod.clicktrack.utils.optionalCast

class ClickTrackPlayerEpic(
    private val store: Store<AppState>,
    private val clickTrackPlayer: ClickTrackPlayer
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return store.state
            .map { it.backstack.frontScreen().optionalCast<Screen.ClickTrack>()?.state }
            .distinctUntilChangedBy { it?.isPlaying }
            .consumeEach { state ->
                if (state == null) {
                    clickTrackPlayer.stop()
                } else {
                    val isPlaying = state.isPlaying
                    val clickTrack = state.clickTrack
                    if (isPlaying) clickTrackPlayer.play(clickTrack.clickTrack) else clickTrackPlayer.stop()
                }
            }
    }
}
