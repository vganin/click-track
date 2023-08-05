package com.vsevolodganin.clicktrack.player

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.toDuration

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
