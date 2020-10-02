package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.DataLoadedAction
import net.ganin.vsevolod.clicktrack.state.actions.LoadDataAction
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository

class LoadDataEpic(
    private val clickTrackRepository: ClickTrackRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions
            .filterIsInstance<LoadDataAction>()
            .flatMapLatest { clickTrackRepository.all() }
            .map(::DataLoadedAction)
    }
}
