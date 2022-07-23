package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
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
}

fun BeatsPerMinute.applyDiff(diff: BeatsPerMinuteDiff): BeatsPerMinute =
    BeatsPerMinute((value + diff.value).coerceAtLeast(MIN_BPM_VALUE))
