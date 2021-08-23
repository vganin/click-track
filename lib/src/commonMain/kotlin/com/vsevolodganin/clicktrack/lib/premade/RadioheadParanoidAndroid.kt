package com.vsevolodganin.clicktrack.lib.premade

import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm

// Source: https://www.songsterr.com/a/wsa/radiohead-paranoid-android-2-drum-tab-s407051t4
@Suppress("unused") // Used for scope
public val PreMadeClickTracks.RadioheadParanoidAndroid: ClickTrack
    get() = ClickTrack(
        name = "Radiohead – Paranoid Android",
        cues = listOf(
            Cue(
                name = "First part",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(45),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(2),
            ),
            Cue(
                name = "Second part",
                bpm = SECOND_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(33),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(4),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(1),
            ),
        ),
        loop = false,
    )

private val FIRST_PART_TEMPO = 82.bpm
private val SECOND_PART_TEMPO = 63.bpm
