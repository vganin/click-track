package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.LoadDataAction
import net.ganin.vsevolod.clicktrack.state.actions.RemoveClickTrack
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository

class RemoveClickTrackEpic(private val storage: ClickTrackRepository) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions
            .filterIsInstance<RemoveClickTrack>()
            .map {
                storage.remove(it.clickTrack)
                LoadDataAction
            }
    }
}
