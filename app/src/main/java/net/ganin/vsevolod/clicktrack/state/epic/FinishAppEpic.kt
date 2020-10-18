package net.ganin.vsevolod.clicktrack.state.epic

import android.app.Activity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import net.ganin.vsevolod.clicktrack.di.component.ActivityScoped
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.actions.FinishApp
import net.ganin.vsevolod.clicktrack.state.actions.NavigateBack
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
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

            actions.filterIsInstance<NavigateBack>()
                .transform {
                    if (store.state.value.backstack.screens.isEmpty()) {
                        emit(FinishApp)
                    }
                }
        )
    }
}
