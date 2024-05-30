package com.vsevolodganin.clicktrack.metronome

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature

fun metronomeClickTrack(
    name: String,
    bpm: BeatsPerMinute,
    pattern: NotePattern,
) = ClickTrack(
    name = name,
    cues = listOf(
        Cue(
            bpm = bpm,
            pattern = pattern,
            timeSignature = MetronomeTimeSignature,
            duration = MetronomeDuration,
        ),
    ),
    loop = true,
)

val MetronomeTimeSignature = TimeSignature(4, 4)
private val MetronomeDuration = CueDuration.Beats(4)
