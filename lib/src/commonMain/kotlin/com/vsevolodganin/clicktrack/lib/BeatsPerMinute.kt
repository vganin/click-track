package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public data class BeatsPerMinute(
    public val value: Int,
) : AndroidParcelable, Comparable<BeatsPerMinute> {

    public constructor(beatCount: Int, timelapse: Duration) : this((Duration.minutes(1) / timelapse * beatCount).roundToInt())

    init {
        require(value >= MIN_BPM_VALUE) { "Bpm should be equal or greater than $MIN_BPM_VALUE but was: $value" }
    }

    override fun compareTo(other: BeatsPerMinute): Int {
        return value.compareTo(other.value)
    }

    public operator fun plus(o: BeatsPerMinute): BeatsPerMinute {
        return BeatsPerMinute(value + o.value)
    }

    public operator fun minus(o: BeatsPerMinute): BeatsPerMinute {
        return BeatsPerMinute(value - o.value)
    }
}

@JvmInline
public value class BeatsPerMinuteDiff(
    public val value: Int,
)

public fun BeatsPerMinute.applyDiff(diff: BeatsPerMinuteDiff): BeatsPerMinute =
    BeatsPerMinute((value + diff.value).coerceAtLeast(MIN_BPM_VALUE))

public val Int.bpm: BeatsPerMinute get() = BeatsPerMinute(this)

public val BeatsPerMinute.interval: Duration
    get() {
        return Duration.minutes(1) / value
    }

private const val MIN_BPM_VALUE = 0
