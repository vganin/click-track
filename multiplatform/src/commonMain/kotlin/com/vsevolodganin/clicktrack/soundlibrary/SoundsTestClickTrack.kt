package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm

fun soundTestClickTrack() =
    ClickTrack(
        name = "",
        cues = listOf(
            Cue(
                bpm = 120.bpm,
                pattern = NotePattern.STRAIGHT_X1,
                timeSignature = SoundsTestTimeSignature,
                duration = SoundsTestDuration,
            ),
        ),
        loop = true,
    )

private val SoundsTestTimeSignature = TimeSignature(4, 4)
private val SoundsTestDuration = CueDuration.Beats(4)
