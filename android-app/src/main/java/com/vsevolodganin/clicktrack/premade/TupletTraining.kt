package com.vsevolodganin.clicktrack.premade

import com.vsevolodganin.clicktrack.model.bpm

@Suppress("unused") // Used for scope
val PreMadeClickTracks.TupletTraining: com.vsevolodganin.clicktrack.model.ClickTrack
    get() = com.vsevolodganin.clicktrack.model.ClickTrack(
        name = "Fun with tuplets",
        cues = listOf(
            com.vsevolodganin.clicktrack.model.Cue(
                name = "Straight",
                bpm = 160.bpm,
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(2),
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                pattern = com.vsevolodganin.clicktrack.model.NotePattern.STRAIGHT_X1,
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                name = "Triplets",
                bpm = 160.bpm,
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(2),
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                pattern = com.vsevolodganin.clicktrack.model.NotePattern.TRIPLET_X1,
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                name = "Quintuplets",
                bpm = 160.bpm,
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(2),
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                pattern = com.vsevolodganin.clicktrack.model.NotePattern.QUINTUPLET_X1,
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                name = "Septuplets",
                bpm = 160.bpm,
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(2),
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                pattern = com.vsevolodganin.clicktrack.model.NotePattern.SEPTUPLET_X1,
            ),
        ),
        loop = true,
    )
