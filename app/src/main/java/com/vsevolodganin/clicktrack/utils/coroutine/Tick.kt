package com.vsevolodganin.clicktrack.utils.coroutine

import android.os.SystemClock
import com.vsevolodganin.clicktrack.lib.utils.collection.toRoundRobin
import kotlin.time.Duration
import kotlin.time.TimeSource

enum class TickDelayMethod {
    THREAD_SLEEP, SUSPEND
}

suspend fun tick(
    duration: Duration,
    interval: Duration,
    onTick: suspend (passed: Duration) -> Unit,
    delayMethod: TickDelayMethod = TickDelayMethod.THREAD_SLEEP,
) {
    require(!duration.isNegative())
    require(!interval.isNegative())
    val startTime = TimeSource.Monotonic.markNow()
    tick(
        durationNanos = duration.toLongNanoseconds(),
        intervalNanos = interval.coerceAtMost(duration).toLongNanoseconds(),
        onTick = { onTick(startTime.elapsedNow()) },
        delayNanos = delayMethod.methodReference(),
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

suspend fun <T> tick(
    duration: Duration,
    pattern: Iterable<T>,
    interval: (T) -> Duration,
    onTick: suspend (passed: Duration, element: T) -> Unit,
    delayMethod: TickDelayMethod = TickDelayMethod.THREAD_SLEEP,
) {
    require(!duration.isNegative())
    val startTime = TimeSource.Monotonic.markNow()
    tick(
        durationNanos = duration.toLongNanoseconds(),
        pattern = pattern,
        interval = { interval(it).coerceAtMost(duration).toLongNanoseconds() },
        onTick = { element -> onTick(startTime.elapsedNow(), element) },
        delayNanos = delayMethod.methodReference(),
    )
}

private suspend fun <T> tick(
    durationNanos: Long,
    pattern: Iterable<T>,
    interval: (T) -> Long,
    onTick: suspend (element: T) -> Unit,
    delayNanos: suspend (Long) -> Unit,
) {
    val patternCycled = pattern.takeIf { it.any() }?.toRoundRobin() ?: return
    val startTime = nanoTime()
    val maxDeadline = startTime + durationNanos
    var deadline = startTime
    var now: Long
    while (deadline < maxDeadline) {
        val patternElement = patternCycled.next() ?: return
        val intervalNanos = interval(patternElement)
        onTick(patternElement)
        deadline = (deadline + intervalNanos).coerceAtMost(maxDeadline)
        now = nanoTime()
        if (now >= deadline) {
            val previousDeadline = (now - startTime) / intervalNanos * intervalNanos + startTime
            deadline = (previousDeadline + intervalNanos).coerceAtMost(maxDeadline)
        }
        delayNanos(deadline - now)
    }
}

private fun TickDelayMethod.methodReference() = when (this) {
    TickDelayMethod.THREAD_SLEEP -> ::delayNanosThreadSleep
    TickDelayMethod.SUSPEND -> ::delayNanosCoroutines
}

private fun nanoTime() = SystemClock.elapsedRealtimeNanos()
