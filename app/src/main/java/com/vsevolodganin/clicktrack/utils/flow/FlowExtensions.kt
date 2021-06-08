package com.vsevolodganin.clicktrack.utils.flow

import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

fun <T> Flow<*>.ignoreElements(): Flow<T> {
    @Suppress("UNCHECKED_CAST") // Consumer should not receive any items
    return filter { false } as Flow<T>
}

fun <T, R> Flow<T>.consumeEach(action: suspend (T) -> Unit): Flow<R> {
    return onEach(action).ignoreElements()
}

fun <T> Flow<T>.takeUntilSignal(signal: Flow<*>): Flow<T> = flow {
    try {
        coroutineScope {
            launch {
                signal.take(1).collect()
                this@coroutineScope.cancel()
            }

            collect {
                emit(it)
            }
        }
    } catch (e: CancellationException) {
        // ignore
    }
}
