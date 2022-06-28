package com.vsevolodganin.clicktrack.redux.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface Epic {
    fun act(actions: Flow<Action>): Flow<Action>
}

class EpicMiddleware<T>(private val coroutineScope: CoroutineScope) : Middleware<T> {

    private val actions = MutableSharedFlow<Action>(
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    private var store: Store<T>? = null

    private val subscriptions = mutableMapOf<Epic, Job>()

    fun register(vararg epics: Epic) {
        for (epic in epics) {
            subscriptions[epic]?.cancel()
            subscriptions[epic] = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                epic.act(actions)
                    .collect { action ->
                        requireNotNull(store).dispatch(action)
                    }
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
        return SuspendDispatch { action ->
            dispatch(action)
            actions.emit(action)
        }
    }
}
