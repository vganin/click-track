package net.ganin.vsevolod.clicktrack.utils.coroutine

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface NonConflatedStateFlow<T> : StateFlow<T>

interface MutableNonConflatedStateFlow<T> : NonConflatedStateFlow<T> {
    override var value: T
}

fun <T> MutableNonConflatedStateFlow(initialValue: T): MutableNonConflatedStateFlow<T> {
    val sharedFlow = MutableSharedFlow<T>(
        replay = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    ).apply { tryEmit(initialValue) }
    return object : MutableNonConflatedStateFlow<T>, MutableSharedFlow<T> by sharedFlow {
        override var value: T
            get() = sharedFlow.replayCache[0]
            set(value) {
                sharedFlow.tryEmit(value)
            }
    }
}
