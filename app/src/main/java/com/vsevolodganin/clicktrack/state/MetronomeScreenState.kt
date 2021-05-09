package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeScreenState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val progress: Double?,
    val isPlaying: Boolean,
    val areOptionsExpanded: Boolean,
) : Parcelable
