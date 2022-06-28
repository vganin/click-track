package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
@Parcelize
data class BeatsPerMinute(
    val value: Int,
) : Parcelable, Comparable<BeatsPerMinute> {

    constructor(beatCount: Int, timelapse: Duration) : this((1.minutes / timelapse * beatCount).roundToInt())

    init {
        require(value >= MIN_BPM_VALUE) { "Bpm should be equal or greater than $MIN_BPM_VALUE but was: $value" }
    }

    override fun compareTo(other: BeatsPerMinute): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(o: BeatsPerMinute): BeatsPerMinute {
        return BeatsPerMinute(value + o.value)
    }

    operator fun minus(o: BeatsPerMinute): BeatsPerMinute {
        return BeatsPerMinute(value - o.value)
    }
}

@JvmInline
value class BeatsPerMinuteDiff(
    val value: Int,
)

fun BeatsPerMinute.applyDiff(diff: BeatsPerMinuteDiff): BeatsPerMinute =
    BeatsPerMinute((value + diff.value).coerceAtLeast(MIN_BPM_VALUE))

val Int.bpm: BeatsPerMinute get() = BeatsPerMinute(this)

fun IntRange.toBpmRange(): ClosedRange<BeatsPerMinute> = start.bpm..endInclusive.bpm

val BeatsPerMinute.interval: Duration
    get() {
        return 1.minutes / value
    }

private const val MIN_BPM_VALUE = 1
