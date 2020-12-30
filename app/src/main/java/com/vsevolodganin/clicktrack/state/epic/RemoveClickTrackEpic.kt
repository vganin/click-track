package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.state.actions.StoreRemoveClickTrack
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
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
