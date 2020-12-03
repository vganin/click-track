package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.StoreRemoveClickTrack
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import javax.inject.Inject

@ViewModelScoped
class RemoveClickTrackEpic @Inject constructor(
    private val storage: ClickTrackRepository
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions
            .filterIsInstance<StoreRemoveClickTrack>()
            .consumeEach {
                storage.remove(it.id)
            }
    }
}
