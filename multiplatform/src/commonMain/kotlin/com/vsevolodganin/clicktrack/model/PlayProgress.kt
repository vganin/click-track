package com.vsevolodganin.clicktrack.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.toDuration

@Serializable
data class PlayProgress(
    private val position: Duration,
    val isPaused: Boolean = false,
    private val emissionTime: PlayableProgressTimeMark = PlayableProgressTimeSource.markNow(),
) {
    val realPosition: Duration get() = if (isPaused) position else position + emissionTime.elapsedNow()
}

@Serializable
data class PlayableProgressTimeMark(private val startedAtMillis: Long) : TimeMark {
    override fun elapsedNow(): Duration {
        return (elapsedRealtimeNanos() - startedAtMillis).toDuration(DurationUnit.NANOSECONDS)
    }

    override fun plus(duration: Duration): PlayableProgressTimeMark {
        return PlayableProgressTimeMark(startedAtMillis + duration.inWholeNanoseconds)
    }

    override fun minus(duration: Duration): TimeMark {
        return PlayableProgressTimeMark(startedAtMillis - duration.inWholeNanoseconds)
    }
}

object PlayableProgressTimeSource : TimeSource {
    override fun markNow(): PlayableProgressTimeMark {
        return PlayableProgressTimeMark(elapsedRealtimeNanos())
    }
}

private fun elapsedRealtimeNanos(): Long = Clock.System.now().run { epochSeconds * 1_000_000_000L + nanosecondsOfSecond }
