package net.ganin.vsevolod.clicktrack.redux

interface Middleware<T> {
    fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch
}
