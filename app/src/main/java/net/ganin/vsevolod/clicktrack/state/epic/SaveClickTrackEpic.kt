package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.SaveClickTrack
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach

class SaveClickTrackEpic(private val storage: ClickTrackRepository) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<SaveClickTrack>()
            .consumeEach {
                storage.put(it.clickTrack)
            }
    }
}
