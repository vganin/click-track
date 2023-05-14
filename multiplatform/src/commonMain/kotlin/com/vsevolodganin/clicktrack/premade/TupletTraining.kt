package com.vsevolodganin.clicktrack.premade

import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm

@Suppress("unused") // Used for scope
val PreMadeClickTracks.TupletTraining: ClickTrack
    get() = ClickTrack(
        name = "Fun with tuplets",
        cues = listOf(
            Cue(
                name = "Straight",
                bpm = 160.bpm,
                duration = CueDuration.Measures(2),
                timeSignature = TimeSignature(4, 4),
                pattern = NotePattern.STRAIGHT_X1,
            ),
            Cue(
                name = "Triplets",
                bpm = 160.bpm,
                duration = CueDuration.Measures(2),
                timeSignature = TimeSignature(4, 4),
                pattern = NotePattern.TRIPLET_X1,
            ),
            Cue(
                name = "Quintuplets",
                bpm = 160.bpm,
                duration = CueDuration.Measures(2),
                timeSignature = TimeSignature(4, 4),
                pattern = NotePattern.QUINTUPLET_X1,
            ),
            Cue(
                name = "Septuplets",
                bpm = 160.bpm,
                duration = CueDuration.Measures(2),
                timeSignature = TimeSignature(4, 4),
                pattern = NotePattern.SEPTUPLET_X1,
            ),
        ),
        loop = true,
    )
