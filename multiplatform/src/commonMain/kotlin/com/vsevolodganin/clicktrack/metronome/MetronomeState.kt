package com.vsevolodganin.clicktrack.metronome

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.PlayProgress

@Parcelize
data class MetronomeState(
    val bpm: BeatsPerMinute,
    val pattern: NotePattern,
    val isPlaying: Boolean,
    val progress: PlayProgress?,
    val areOptionsExpanded: Boolean,
) : Parcelable
