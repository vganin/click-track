package com.vsevolodganin.clicktrack.metronome

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature

fun metronomeClickTrack(name: String, bpm: BeatsPerMinute, pattern: NotePattern, timeSignature: TimeSignature) = ClickTrack(
    name = name,
    cues = listOf(
        Cue(
            bpm = bpm,
            pattern = pattern,
            timeSignature = timeSignature,
            duration = CueDuration.Beats(timeSignature.noteCount),
        ),
    ),
    loop = true,
)
