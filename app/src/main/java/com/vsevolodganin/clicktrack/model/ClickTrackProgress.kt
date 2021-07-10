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
data class ClickTrackProgress(
    val value: Double,
    val generationTimeMark: ClickTimeProgressTimeMark = ClickTimeProgressTimeSource.markNow(),
) : Parcelable

@Parcelize
data class ClickTimeProgressTimeMark(private val startedAt: Long) : TimeMark(), Parcelable {
    override fun elapsedNow(): Duration {
        return (elapsedRealtimeNanos() - startedAt).toDuration(DurationUnit.NANOSECONDS)
    }
}

object ClickTimeProgressTimeSource : TimeSource {
    override fun markNow(): ClickTimeProgressTimeMark {
        return ClickTimeProgressTimeMark(elapsedRealtimeNanos())
    }
}

private fun elapsedRealtimeNanos(): Long = SystemClock.elapsedRealtimeNanos()
