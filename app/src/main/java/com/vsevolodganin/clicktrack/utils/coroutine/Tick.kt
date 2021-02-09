package com.vsevolodganin.clicktrack.utils.coroutine

import android.os.SystemClock
import kotlinx.coroutines.runInterruptible
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.nanoseconds

suspend fun tick(
    duration: Duration,
    interval: Duration,
    onTick: suspend (passed: Duration) -> Unit,
) {
    require(!duration.isNegative())
    require(!interval.isNegative())
    val startTime = TimeSource.Monotonic.markNow()
    tick(
        durationNanos = duration.toLongNanoseconds(),
        intervalNanos = interval.coerceAtMost(duration).toLongNanoseconds(),
        onTick = { onTick(startTime.elapsedNow()) },
        delayNanos = { delayNanosThreadSleep(it) },
    )
}

private suspend fun tick(
    durationNanos: Long,
    intervalNanos: Long,
    onTick: suspend () -> Unit,
    delayNanos: suspend (Long) -> Unit,
) {
    val startTime = nanoTime()
    val maxDeadline = startTime + durationNanos
    var deadline = startTime
    var now: Long
    while (deadline < maxDeadline) {
        onTick()
        deadline = (deadline + intervalNanos).coerceAtMost(maxDeadline)
        now = nanoTime()
        if (now >= deadline) {
            val previousDeadline = (now - startTime) / intervalNanos * intervalNanos + startTime
            deadline = (previousDeadline + intervalNanos).coerceAtMost(maxDeadline)
        }
        delayNanos(deadline - now)
    }
}

@Suppress("unused")
private suspend fun delayNanosCoroutines(timeNanos: Long) {
    delay(timeNanos.nanoseconds)
}

@Suppress("unused")
private suspend fun delayNanosThreadSleep(timeNanos: Long) {
    if (timeNanos <= 0) return
    runInterruptible {
        Thread.sleep(timeNanos / NANOS_IN_SECOND, (timeNanos % NANOS_IN_SECOND).toInt())
    }
}

private fun nanoTime() = SystemClock.elapsedRealtimeNanos()

private const val NANOS_IN_SECOND = 1_000_000L
