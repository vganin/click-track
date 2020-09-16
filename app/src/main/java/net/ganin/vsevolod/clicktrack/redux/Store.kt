package net.ganin.vsevolod.clicktrack.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

typealias Dispatch = (action: Action) -> Unit
typealias SuspendDispatch = suspend (action: Action) -> Unit

class Store<T>(
    initialState: T,
    reducer: Reducer<T>,
    private val storeCoroutineScope: CoroutineScope,
    vararg middlewares: Middleware<T>
) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> = _state

    private val actions = Channel<Action>()

    init {
        val initialDispatch: SuspendDispatch = { action: Action ->
            _state.value = reducer(_state.value, action)
        }
        val dispatch = middlewares.fold(initialDispatch) { dispatch, middleware ->
            middleware.interfere(this@Store, dispatch)
        }

        storeCoroutineScope.launch {
            actions.consumeAsFlow().collect(dispatch)
        }
    }

    fun dispatch(action: Action) {
        storeCoroutineScope.launch {
            actions.send(action)
        }
    }
}
