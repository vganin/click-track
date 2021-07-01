package com.vsevolodganin.clicktrack.state.redux.core

import timber.log.Timber

class DebugMiddleware<T> : Middleware<T> {

    override fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch {
        return if (DEBUG) {
            log("Initial store state:\n${store.state.value}")
            SuspendDispatch { action ->
                dispatch(action)
                log("Store state after action $action:\n${store.state.value}")
            }
        } else {
            dispatch
        }
    }

    private fun log(message: String) = Timber.tag(TAG).d(message)
}

private const val TAG = "DebugMiddleware"
private const val DEBUG = false
