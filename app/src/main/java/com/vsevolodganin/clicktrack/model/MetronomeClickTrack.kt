package com.vsevolodganin.clicktrack.model

fun metronomeClickTrack(
    name: String,
    bpm: BeatsPerMinute,
    pattern: NotePattern,
): ClickTrackWithId {
    return ClickTrackWithSpecificId(
        id = ClickTrackId.Builtin.Metronome,
        value = ClickTrack(
            name = name,
            cues = listOf(
                Cue(
                bpm = bpm,
                pattern = pattern,
                timeSignature = MetronomeTimeSignature,
                duration = MetronomeDuration,
            )
            ),
            loop = true,
        )
    )
}

val MetronomeTimeSignature = TimeSignature(4, 4)
val MetronomeDuration = CueDuration.Beats(4)
