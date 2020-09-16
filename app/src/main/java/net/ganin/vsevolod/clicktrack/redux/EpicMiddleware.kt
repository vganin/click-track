package net.ganin.vsevolod.clicktrack.redux

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface Epic {
    fun act(actions: Flow<Action>): Flow<Action>
}

class EpicMiddleware<T>(private val epicCoroutineContext: CoroutineContext) : Middleware<T> {

    private val actions = BroadcastChannel<Action>(1)
    private var store: Store<T>? = null

    fun register(vararg epics: Epic) {
        for (epic in epics) {
            GlobalScope.launch(Dispatchers.Unconfined) {
                epic.act(actions.asFlow())
                    .onEach { action ->
                        val store = store
                        requireNotNull(store)
                        store.dispatch(action)
                    }
                    .flowOn(epicCoroutineContext)
                    .collect()
            }
        }
    }

    override fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch {
        require(this.store == null) { "Interfering twice" }
        this.store = store
        return { action ->
            dispatch(action)
            actions.send(action)
        }
    }
}
