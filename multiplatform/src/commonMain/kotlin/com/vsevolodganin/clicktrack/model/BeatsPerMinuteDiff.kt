package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

// TODO: Make `value class` with @JvmInline with Kotlin 1.7.20 and `kapt.use.jvm.ir=true` (see https://youtrack.jetbrains.com/issue/KT-49682)
@Parcelize
@Serializable
class BeatsPerMinuteDiff(
    val value: Int,
) : Parcelable {
    companion object {
        val ZERO = BeatsPerMinuteDiff(0)
    }

    operator fun plus(o: Int) = BeatsPerMinuteDiff(value + o)
    operator fun minus(o: Int) = BeatsPerMinuteDiff(value - o)
    operator fun plus(o: BeatsPerMinuteDiff) = (value + o.value).bpm
    operator fun minus(o: BeatsPerMinuteDiff) = (value - o.value).bpm
}
