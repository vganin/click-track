package net.ganin.vsevolod.clicktrack.state.epic

import android.app.Activity
import kotlinx.coroutines.flow.*
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.actions.FinishApp
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import kotlin.coroutines.CoroutineContext

class FinishAppEpic(
    private val activity: Activity,
    private val store: Store<AppState>,
    private val mainCoroutineContext: CoroutineContext
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<FinishApp>()
                .consumeEach<FinishApp, Action> { activity.finish() }
                .flowOn(mainCoroutineContext),

            actions.filterIsInstance<NavigateBack>()
                .transform {
                    if (store.state.value.backstack.screens.isEmpty()) {
                        emit(FinishApp)
                    }
                }
        )
    }
}
