package com.vsevolodganin.clicktrack.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

interface Epic {
    fun act(actions: Flow<Action>): Flow<Action>
}

class EpicMiddleware<T>(private val coroutineScope: CoroutineScope) : Middleware<T> {

    private val actions = Channel<Action>(RENDEZVOUS)
    private var store: Store<T>? = null

    private val subscriptions = mutableMapOf<Epic, Job>()

    fun register(vararg epics: Epic) {
        val actionsFlow = actions.consumeAsFlow().shareIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly
        )

        for (epic in epics) {
            subscriptions[epic]?.cancel()
            subscriptions[epic] = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
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
