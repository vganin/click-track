package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.DataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.LoadDataAction
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository

class LoadDataEpic(
    private val store: Store<AppState>,
    private val clickTrackRepository: ClickTrackRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .transform { state ->
                    val frontScreen = state.backstack.frontScreen()
                    if (frontScreen is Screen.ClickTrackList) {
                        emit(LoadDataAction)
                    }
                },
            actions
                .filterIsInstance<LoadDataAction>()
                .map { clickTrackRepository.all() }
                .map(::DataLoadedAction),
        )
    }
}
