package com.vsevolodganin.clicktrack.utils.coroutine

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.TimeSource

suspend fun tick(
    duration: Duration,
    interval: Duration,
    initialDelay: Duration,
    onTick: suspend (passed: Duration) -> Unit,
) {
    val startTime = TimeSource.Monotonic.markNow()
    try {
        withTimeout(duration) {
            kotlinx.coroutines.channels.ticker(
                delayMillis = interval.toLongMilliseconds(),
                initialDelayMillis = initialDelay.toLongMilliseconds(),
            ).consumeEach {
                onTick(startTime.elapsedNow())
            }
        }
    } catch (e: TimeoutCancellationException) {
        // Ignore
    }
}
