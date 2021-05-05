package com.vsevolodganin.clicktrack.utils.coroutine

import android.os.SystemClock
import com.vsevolodganin.clicktrack.lib.utils.collection.toRoundRobin
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.nanoseconds
import kotlinx.coroutines.runInterruptible

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
    objects: Iterable<T>,
    intervalSelector: (T) -> Duration,
    onTick: suspend (passed: Duration, `object`: T) -> Unit,
    delayMethod: TickDelayMethod = TickDelayMethod.THREAD_SLEEP,
) {
    require(!duration.isNegative())
    val startTime = TimeSource.Monotonic.markNow()
    tick(
        durationNanos = duration.toLongNanoseconds(),
        objects = objects,
        intervalNanosSelector = { intervalSelector(it).coerceAtMost(duration).toLongNanoseconds() },
        onTick = { `object` -> onTick(startTime.elapsedNow(), `object`) },
        delayNanos = delayMethod.methodReference(),
    )
}

private suspend fun <T> tick(
    durationNanos: Long,
    objects: Iterable<T>,
    intervalNanosSelector: (T) -> Long,
    onTick: suspend (`object`: T) -> Unit,
    delayNanos: suspend (Long) -> Unit,
) {
    val objectsRoundRobin = objects.takeIf { it.any() }?.toRoundRobin() ?: return
    val startTime = nanoTime()
    val maxDeadline = startTime + durationNanos
    var deadline = startTime
    var now: Long
    while (deadline < maxDeadline) {
        val `object` = objectsRoundRobin.next() ?: return
        val intervalNanos = intervalNanosSelector(`object`)
        onTick(`object`)
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
