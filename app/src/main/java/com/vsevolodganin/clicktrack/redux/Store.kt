package com.vsevolodganin.clicktrack.redux

import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.NonConflatedStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

fun interface Dispatch {
    operator fun invoke(action: Action)
}

interface SuspendDispatch {
    suspend operator fun invoke(action: Action)
}

class Store<T>(
    initialState: T,
    reducer: Reducer<T>,
    coroutineScope: CoroutineScope,
    vararg middlewares: Middleware<T>,
) {
    private val _state = MutableNonConflatedStateFlow(initialState)
    val state: NonConflatedStateFlow<T> = _state

    private val actions = Channel<Action>(UNLIMITED)

    init {
        val initialDispatch: SuspendDispatch = object : SuspendDispatch {
            override suspend fun invoke(action: Action) {
                _state.setValue(reducer(_state.value, action))
            }
        }
        val dispatch = middlewares.fold(initialDispatch) { dispatch, middleware ->
            middleware.interfere(this@Store, dispatch)
        }

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            actions.consumeAsFlow().collect { action -> dispatch(action) }
        }
    }

    fun dispatch(action: Action) {
        actions.trySend(action)
    }
}
