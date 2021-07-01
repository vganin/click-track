package com.vsevolodganin.clicktrack.state.redux.core

interface Middleware<T> {
    fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch
}
