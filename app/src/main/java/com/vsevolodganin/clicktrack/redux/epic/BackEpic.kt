package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.module.ActivityScopedAppStateEpic
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.action.BackAction
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.DrawerAction
import com.vsevolodganin.clicktrack.redux.action.FinishApp
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityScopedAppStateEpic
class BackEpic @Inject constructor(
    private val store: Store<AppState>,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<BackAction>()
            .map {
                val state = store.state.value
                if (state.drawerState.isOpened) {
                    DrawerAction.Close
                } else if (state.backstack.restScreens.isNotEmpty()) {
                    BackstackAction.Pop
                } else {
                    FinishApp
                }
            }
    }
}
