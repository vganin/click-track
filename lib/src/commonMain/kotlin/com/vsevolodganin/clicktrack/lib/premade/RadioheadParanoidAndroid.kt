package com.vsevolodganin.clicktrack.lib.premade

import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.CueWithDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm

// Source: https://www.songsterr.com/a/wsa/radiohead-paranoid-android-2-drum-tab-s407051t4
public val PreMadeClickTracks.RadioheadParanoidAndroid: ClickTrack
    get() = ClickTrack(
        name = "Radiohead – Paranoid Android",
        cues = listOf(
            // First part
            // 1-45
            CueWithDuration(
                duration = CueDuration.Measures(45),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 45-48
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 49-53
            CueWithDuration(
                duration = CueDuration.Measures(5),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 54-56
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 57-61
            CueWithDuration(
                duration = CueDuration.Measures(5),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 62-64
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 65-69
            CueWithDuration(
                duration = CueDuration.Measures(5),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 70-72
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 73-74
            CueWithDuration(
                duration = CueDuration.Measures(2),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // Second part
            // 75-107
            CueWithDuration(
                duration = CueDuration.Measures(33),
                cue = Cue(
                    bpm = SECOND_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 108-111
            CueWithDuration(
                duration = CueDuration.Measures(4),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 112-114
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 115-119
            CueWithDuration(
                duration = CueDuration.Measures(5),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            // 120-122
            CueWithDuration(
                duration = CueDuration.Measures(3),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(7, 8)
                )
            ),
            // 123
            CueWithDuration(
                duration = CueDuration.Measures(1),
                cue = Cue(
                    bpm = FIRST_PART_TEMPO,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
        ),
        loop = false,
        sounds = BuiltinClickSounds
    )

private val FIRST_PART_TEMPO = 82.bpm
private val SECOND_PART_TEMPO = 63.bpm
