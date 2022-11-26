package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import android.os.SystemClock
import com.vsevolodganin.clicktrack.utils.time.DurationParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
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
data class PlayableProgressTimeMark(private val startedAtNanos: Long) : TimeMark, Parcelable {
    override fun elapsedNow(): Duration {
        return (elapsedRealtimeNanos() - startedAtNanos).toDuration(DurationUnit.NANOSECONDS)
    }

    override fun plus(duration: Duration): PlayableProgressTimeMark {
        return PlayableProgressTimeMark(startedAtNanos + duration.inWholeNanoseconds)
    }

    override fun minus(duration: Duration): TimeMark {
        return PlayableProgressTimeMark(startedAtNanos - duration.inWholeNanoseconds)
    }
}

object PlayableProgressTimeSource : TimeSource {
    override fun markNow(): PlayableProgressTimeMark {
        return PlayableProgressTimeMark(elapsedRealtimeNanos())
    }
}

private fun elapsedRealtimeNanos(): Long = SystemClock.elapsedRealtimeNanos()
