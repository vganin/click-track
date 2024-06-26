package com.vsevolodganin.clicktrack.model

import kotlinx.serialization.Serializable

// TODO: Make `value class` with @JvmInline with Kotlin 1.7.20 and `kapt.use.jvm.ir=true` (see https://youtrack.jetbrains.com/issue/KT-49682)
@Serializable
data class BeatsPerMinuteOffset(
    val value: Int,
) {
    companion object {
        val ZERO = BeatsPerMinuteOffset(0)
    }

    operator fun plus(o: Int) = BeatsPerMinuteOffset(value + o)

    operator fun minus(o: Int) = BeatsPerMinuteOffset(value - o)

    operator fun plus(o: BeatsPerMinuteOffset) = (value + o.value).bpm

    operator fun minus(o: BeatsPerMinuteOffset) = (value - o.value).bpm
}
