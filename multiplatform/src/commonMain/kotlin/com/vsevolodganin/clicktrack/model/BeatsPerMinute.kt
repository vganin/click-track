package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
@Parcelize
data class BeatsPerMinute(val value: Int) : Parcelable, Comparable<BeatsPerMinute> {

    companion object {
        val VALID_TEMPO_RANGE = 1..999
    }

    init {
        require(value in VALID_TEMPO_RANGE) {
            "Bpm should be in range [${VALID_TEMPO_RANGE.first}, $${VALID_TEMPO_RANGE.last}] but was: $value"
        }
    }

    override fun compareTo(other: BeatsPerMinute): Int = value.compareTo(other.value)
    operator fun plus(o: BeatsPerMinute): BeatsPerMinute = (value + o.value).bpm
    operator fun minus(o: BeatsPerMinute): BeatsPerMinute = (value - o.value).bpm
    operator fun plus(o: BeatsPerMinuteDiff): BeatsPerMinute = (value + o.value).bpm
    operator fun minus(o: BeatsPerMinuteDiff): BeatsPerMinute = (value - o.value).bpm
}

fun BeatsPerMinute(beatCount: Int, timelapse: Duration): BeatsPerMinute {
    return (1.minutes / timelapse * beatCount).roundToInt().bpm
}

val Int.bpm: BeatsPerMinute get() = BeatsPerMinute(coerceIn(BeatsPerMinute.VALID_TEMPO_RANGE))

val BeatsPerMinute.interval: Duration get() = 1.minutes / value
