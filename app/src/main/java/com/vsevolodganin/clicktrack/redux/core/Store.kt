package com.vsevolodganin.clicktrack.redux.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

fun interface Dispatch {
    operator fun invoke(action: Action)
}

fun interface SuspendDispatch {
    suspend operator fun invoke(action: Action)
}

class Store<T>(
    initialState: T,
    reducer: Reducer<T>,
    coroutineScope: CoroutineScope,
    vararg middlewares: Middleware<T>,
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> = _state

    private val actions = Channel<Action>(UNLIMITED)

    init {
        val initialDispatch = SuspendDispatch { action ->
            _state.value = reducer(_state.value, action)
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
