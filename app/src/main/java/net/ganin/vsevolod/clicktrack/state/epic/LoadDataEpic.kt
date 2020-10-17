package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackDataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackListDataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackListLoadRequestAction
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackLoadRequestAction
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository

class LoadDataEpic(
    private val store: Store<AppState>,
    private val clickTrackRepository: ClickTrackRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .filterIsInstance<Screen.ClickTrackList>()
                .map { ClickTrackListLoadRequestAction },

            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .filterIsInstance<Screen.ClickTrack>()
                .map { ClickTrackLoadRequestAction(it.state.clickTrack.id) },

            actions
                .filterIsInstance<ClickTrackListLoadRequestAction>()
                .map { clickTrackRepository.getAll() }
                .map(::ClickTrackListDataLoadedAction),

            actions
                .filterIsInstance<ClickTrackLoadRequestAction>()
                .mapNotNull { clickTrackRepository.get(it.id) }
                .map(::ClickTrackDataLoadedAction),
        )
    }
}
