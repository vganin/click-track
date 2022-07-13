package com.vsevolodganin.clicktrack.utils.coroutine

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface NonConflatedStateFlow<T> : SharedFlow<T> {
    val value: T
}

interface MutableNonConflatedStateFlow<T> : NonConflatedStateFlow<T>, MutableSharedFlow<T> {
    override var value: T
}

@Suppress("FunctionName") // Factory function
fun <T> MutableNonConflatedStateFlow(initialValue: T): MutableNonConflatedStateFlow<T> {
    val sharedFlow = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = Int.MAX_VALUE
    ).apply { tryEmit(initialValue) }

    return object : MutableNonConflatedStateFlow<T>, MutableSharedFlow<T> by sharedFlow {

        override var value: T
            get() = sharedFlow.replayCache[0]
            set(value) {
                sharedFlow.tryEmit(value)
            }
    }
}
