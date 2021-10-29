package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import android.os.SystemClock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.toDuration
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayableProgress(
    val value: Double,
    val totalDuration: Duration,
    val generationTimeMark: PlayableProgressTimeMark = PlayableProgressTimeSource.markNow(),
) : Parcelable

@Parcelize
data class PlayableProgressTimeMark(private val startedAt: Long) : TimeMark(), Parcelable {
    override fun elapsedNow(): Duration {
        return (elapsedRealtimeNanos() - startedAt).toDuration(DurationUnit.NANOSECONDS)
    }
}

object PlayableProgressTimeSource : TimeSource {
    override fun markNow(): PlayableProgressTimeMark {
        return PlayableProgressTimeMark(elapsedRealtimeNanos())
    }
}

private fun elapsedRealtimeNanos(): Long = SystemClock.elapsedRealtimeNanos()
