package com.vsevolodganin.clicktrack.redux.core

interface Middleware<T> {
    fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch
}
