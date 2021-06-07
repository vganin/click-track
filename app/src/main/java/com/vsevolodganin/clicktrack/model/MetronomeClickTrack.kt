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
    return ClickTrackWithId(
        id = ClickTrackId.Builtin.METRONOME,
        value = ClickTrack(
            name = name,
            cues = listOf(Cue(
                bpm = bpm,
                pattern = pattern,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Beats(4),
            )),
            loop = true,
        )
    )
}
