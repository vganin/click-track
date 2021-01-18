package com.vsevolodganin.clicktrack.lib.premade

import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm

// Source: https://www.songsterr.com/a/wsa/radiohead-paranoid-android-2-drum-tab-s407051t4
public val PreMadeClickTracks.RadioheadParanoidAndroid: ClickTrack
    get() = ClickTrack(
        name = "Radiohead – Paranoid Android",
        cues = listOf(
            // First part
            // 1-45
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(45),
            ),
            // 45-48
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 49-53
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            // 54-56
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 57-61
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            // 62-64
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 65-69
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            // 70-72
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 73-74
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(2),
            ),
            // Second part
            // 75-107
            Cue(
                bpm = SECOND_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(33),
            ),
            // 108-111
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(4),
            ),
            // 112-114
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 115-119
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            // 120-122
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            // 123
            Cue(
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(1),
            ),
        ),
        loop = false,
        sounds = BuiltinClickSounds
    )

private val FIRST_PART_TEMPO = 82.bpm
private val SECOND_PART_TEMPO = 63.bpm
