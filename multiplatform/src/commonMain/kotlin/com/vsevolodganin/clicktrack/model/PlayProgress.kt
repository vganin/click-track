package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.TypeParceler
import com.vsevolodganin.clicktrack.utils.time.DurationParceler
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.toDuration

@Parcelize
@TypeParceler<Duration, DurationParceler>
data class PlayProgress(
    private val position: Duration,
    val isPaused: Boolean = false,
    private val emissionTime: PlayableProgressTimeMark = PlayableProgressTimeSource.markNow()
) : Parcelable {
    val realPosition: Duration get() = if (isPaused) position else position + emissionTime.elapsedNow()
}

@Parcelize
data class PlayableProgressTimeMark(private val startedAtMillis: Long) : TimeMark, Parcelable {
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
