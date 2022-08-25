package com.vsevolodganin.clicktrack.utils.coroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest

suspend fun <T> Flow<T>.collectLatestFirst(action: suspend (value: T) -> Unit) {
    mapLatest(action).firstOrNull()
}
