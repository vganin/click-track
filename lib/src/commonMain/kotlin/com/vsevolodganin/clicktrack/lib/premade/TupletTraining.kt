package com.vsevolodganin.clicktrack.lib.premade

import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm

@Suppress("unused") // Used for scope
public val PreMadeClickTracks.TupletTraining: ClickTrack
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
