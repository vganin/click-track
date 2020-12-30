package com.vsevolodganin.clicktrack.utils.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach

fun <T> Flow<*>.ignoreElements(): Flow<T> {
    @Suppress("UNCHECKED_CAST") // Consumer should not receive any items
    return filter { false } as Flow<T>
}

fun <T, R> Flow<T>.consumeEach(action: suspend (T) -> Unit): Flow<R> {
    return onEach(action).ignoreElements()
}
