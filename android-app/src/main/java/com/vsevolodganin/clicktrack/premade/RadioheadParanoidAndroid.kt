package com.vsevolodganin.clicktrack.premade

import com.vsevolodganin.clicktrack.model.bpm

// Source: https://www.songsterr.com/a/wsa/radiohead-paranoid-android-2-drum-tab-s407051t4
@Suppress("unused") // Used for scope
val PreMadeClickTracks.RadioheadParanoidAndroid: com.vsevolodganin.clicktrack.model.ClickTrack
    get() = com.vsevolodganin.clicktrack.model.ClickTrack(
        name = "Radiohead – Paranoid Android",
        cues = listOf(
            com.vsevolodganin.clicktrack.model.Cue(
                name = "First part",
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(45),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(5),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(5),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(5),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(2),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                name = "Second part",
                bpm = SECOND_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(33),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(4),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(5),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(7, 8),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(3),
            ),
            com.vsevolodganin.clicktrack.model.Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = com.vsevolodganin.clicktrack.model.TimeSignature(4, 4),
                duration = com.vsevolodganin.clicktrack.model.CueDuration.Measures(1),
            ),
        ),
        loop = false,
    )

private val FIRST_PART_TEMPO = 82.bpm
private val SECOND_PART_TEMPO = 63.bpm
