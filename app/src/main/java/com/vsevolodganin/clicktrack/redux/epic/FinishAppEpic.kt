package com.vsevolodganin.clicktrack.redux.epic

import android.app.Activity
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.redux.action.FinishApp
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ActivityScoped
class FinishAppEpic @Inject constructor(
    private val activity: Activity,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<FinishApp>()
                .consumeEach<FinishApp, Action> { activity.finish() }
                .flowOn(mainDispatcher),
        )
    }
}
