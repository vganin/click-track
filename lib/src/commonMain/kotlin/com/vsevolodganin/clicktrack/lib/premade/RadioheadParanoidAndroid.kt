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
            Cue(
                name = "First part: 1-45",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(45),
            ),
            Cue(
                name = "45-48",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "49-53",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                name = "54-56",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "57-61",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                name = "62-64",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "65-69",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                name = "70-72",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "73-74",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(2),
            ),
            Cue(
                name = "Second part: 75-107",
                bpm = SECOND_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(33),
            ),
            Cue(
                name = "108-111",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(4),
            ),
            Cue(
                name = "112-114",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "115-119",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(5),
            ),
            Cue(
                name = "120-122",
                bpm = FIRST_PART_TEMPO,
                timeSignature = TimeSignature(7, 8),
                duration = CueDuration.Measures(3),
            ),
            Cue(
                name = "123",
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
