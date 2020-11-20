package net.ganin.vsevolod.clicktrack.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import net.ganin.vsevolod.clicktrack.utils.coroutine.NonConflatedStateFlow

fun interface Dispatch {
    operator fun invoke(action: Action)
}

interface SuspendDispatch {
    suspend operator fun invoke(action: Action)
}

class Store<T>(
    initialState: T,
    reducer: Reducer<T>,
    private val storeCoroutineScope: CoroutineScope,
    vararg middlewares: Middleware<T>
) {
    private val _state = MutableNonConflatedStateFlow(initialState)
    val state: NonConflatedStateFlow<T> = _state

    private val actions = Channel<Action>()

    init {
        val initialDispatch: SuspendDispatch = object : SuspendDispatch {
            override suspend fun invoke(action: Action) {
                _state.value = reducer(_state.value, action)
            }
        }
        val dispatch = middlewares.fold(initialDispatch) { dispatch, middleware ->
            middleware.interfere(this@Store, dispatch)
        }

        storeCoroutineScope.launch {
            actions.consumeAsFlow().collect { action -> dispatch(action) }
        }
    }

    fun dispatch(action: Action) {
        storeCoroutineScope.launch {
            actions.send(action)
        }
    }
}
