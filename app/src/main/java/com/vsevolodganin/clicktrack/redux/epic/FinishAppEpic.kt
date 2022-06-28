package com.vsevolodganin.clicktrack.redux.epic

import android.app.Activity
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.action.FinishApp
import com.vsevolodganin.clicktrack.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.Store
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@ActivityScoped
class FinishAppEpic @Inject constructor(
    private val activity: Activity,
    private val store: Store<AppState>,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<FinishApp>()
                .consumeEach<FinishApp, Action> { activity.finish() }
                .flowOn(mainDispatcher),

            actions.filterIsInstance<NavigationAction.Back>()
                .transform {
                    if (store.state.value.backstack.screens.isEmpty()) {
                        emit(FinishApp)
                    }
                }
        )
    }
}
