package com.vsevolodganin.clicktrack.utils.coroutine

import kotlin.time.nanoseconds
import kotlinx.coroutines.runInterruptible

suspend fun delayNanosCoroutines(timeNanos: Long) {
    delay(timeNanos.nanoseconds)
}

suspend fun delayNanosThreadSleep(timeNanos: Long) {
    if (timeNanos <= 0) return
    runInterruptible {
        Thread.sleep(timeNanos / NANOS_IN_SECOND, (timeNanos % NANOS_IN_SECOND).toInt())
    }
}

private const val NANOS_IN_SECOND = 1_000_000L
