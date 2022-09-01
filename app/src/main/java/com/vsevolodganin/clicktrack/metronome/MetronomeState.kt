package com.vsevolodganin.clicktrack.metronome

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetronomeState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: PlayProgress?,
    val areOptionsExpanded: Boolean,
) : Parcelable
