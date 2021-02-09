package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeScreenState(
    val bpm: BeatsPerMinute,
    val progress: Double?,
    val isPlaying: Boolean,
) : Parcelable
