package com.vsevolodganin.clicktrack.premade

import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration.Measures
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm

// Source: https://www.songsterr.com/a/wsa/radiohead-paranoid-android-2-drum-tab-s407051t4
@Suppress("unused") // Used for scope
val PreMadeClickTracks.RadioheadParanoidAndroid: ClickTrack
    get() = ClickTrack(
        name = "Radiohead – Paranoid Android",
        cues = listOf(
            Cue(
                name = "First part",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(45),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(2),
            ),
            Cue(
                name = "Second part",
                bpm = SECOND_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(33),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(4),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(5),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = Measures(3),
            ),
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = Measures(1),
            ),
        ),
        loop = false,
    )

private val FIRST_PART_TEMPO = 82.bpm
private val SECOND_PART_TEMPO = 63.bpm
