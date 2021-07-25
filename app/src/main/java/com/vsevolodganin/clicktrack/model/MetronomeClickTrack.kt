package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature

fun metronomeClickTrack(
    name: String,
    bpm: BeatsPerMinute,
    pattern: NotePattern,
): ClickTrackWithId {
    return ClickTrackWithSpecificId(
        id = ClickTrackId.Builtin.Metronome,
        value = ClickTrack(
            name = name,
            cues = listOf(Cue(
                bpm = bpm,
                pattern = pattern,
                timeSignature = MetronomeTimeSignature,
                duration = MetronomeDuration,
            )),
            loop = true,
        )
    )
}

val MetronomeTimeSignature = TimeSignature(4, 4)
val MetronomeDuration = CueDuration.Beats(4)
