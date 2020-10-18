package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.ClickTrackListLoadRequestAction
import net.ganin.vsevolod.clicktrack.state.actions.RemoveClickTrack
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
import javax.inject.Inject

@ViewModelScoped
class RemoveClickTrackEpic @Inject constructor(
    private val storage: ClickTrackRepository
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions
            .filterIsInstance<RemoveClickTrack>()
            .map {
                storage.remove(it.id)
                ClickTrackListLoadRequestAction
            }
    }
}
