package com.vsevolodganin.clicktrack.utils.coroutine

import android.os.SystemClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.runInterruptible

suspend fun delayTillDeadlineUsingCoroutines(deadlineNanos: Long) {
    delay((deadlineNanos - nanoTime()) / NANOS_IN_MILLISECOND)
}

suspend fun delayTillDeadlineUsingThreadSleep(deadlineNanos: Long) {
    threadSleepInterruptible(deadlineNanos)
}

suspend fun delayTillDeadlineUsingThreadSleepAndSpinLock(deadlineNanos: Long) {
    threadSleepInterruptible(deadlineNanos - MAX_NANOS_FOR_SPINLOCK)
    spinTillDeadline(deadlineNanos)
}

private suspend fun threadSleepInterruptible(deadlineNanos: Long) {
    if (deadlineNanos - nanoTime() <= 0L) return // Avoid runInterruptible if possible
    runInterruptible {
        val delayNanos = deadlineNanos - nanoTime()
        if (delayNanos <= 0L) return@runInterruptible
        Thread.sleep(delayNanos / NANOS_IN_MILLISECOND, (delayNanos % NANOS_IN_MILLISECOND).toInt())
    }
}

private fun spinTillDeadline(deadlineNanos: Long) {
    @Suppress("ControlFlowWithEmptyBody") // This is mindless spinning by design
    while (deadlineNanos > nanoTime());
}

private fun nanoTime() = SystemClock.elapsedRealtimeNanos()

private const val NANOS_IN_MILLISECOND = 1_000_000L
private const val MAX_NANOS_FOR_SPINLOCK = 1_000_000_000L
