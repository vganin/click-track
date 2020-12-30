package com.vsevolodganin.clicktrack.redux

interface Middleware<T> {
    fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch
}
