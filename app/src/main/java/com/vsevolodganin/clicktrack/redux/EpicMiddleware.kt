package com.vsevolodganin.clicktrack.redux

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface Epic {
    fun act(actions: Flow<Action>): Flow<Action>
}

class EpicMiddleware<T>(private val epicCoroutineContext: CoroutineContext) : Middleware<T> {

    private val actions = BroadcastChannel<Action>(1)
    private var store: Store<T>? = null

    private val subscriptions = mutableMapOf<Epic, Job>()

    fun register(vararg epics: Epic) {
        @Suppress("DEPRECATION") // FIXME: Adopt StateFlow or SharedFlow
        val actionsFlow = actions.asFlow()

        for (epic in epics) {
            subscriptions[epic]?.cancel()
            subscriptions[epic] = GlobalScope.launch(epicCoroutineContext) {
                epic.act(actionsFlow)
                    .onEach { action ->
                        val store = store
                        requireNotNull(store)
                        store.dispatch(action)
                    }
                    .collect()
            }
        }
    }

    fun unregister(vararg epics: Epic) {
        for (epic in epics) {
            subscriptions[epic]?.cancel()
            subscriptions.remove(epic)
        }
    }

    override fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch {
        require(this.store == null) { "Interfering twice" }
        this.store = store
        return object : SuspendDispatch {
            override suspend fun invoke(action: Action) {
                dispatch(action)
                actions.send(action)
            }
        }
    }
}
