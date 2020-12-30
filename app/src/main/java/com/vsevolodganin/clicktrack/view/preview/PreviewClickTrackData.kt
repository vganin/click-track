package com.vsevolodganin.clicktrack.view.preview

import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.CueWithDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId

val PREVIEW_CLICK_TRACK_1 = ClickTrackWithId(
    id = 1,
    value = ClickTrack(
        name = "Preview 1",
        cues = listOf(
            CueWithDuration(
                duration = CueDuration.Beats(4),
                cue = Cue(
                    bpm = 60.bpm,
                    timeSignature = TimeSignature(3, 4)
                )
            ),
            CueWithDuration(
                duration = CueDuration.Beats(4),
                cue = Cue(
                    bpm = 150.bpm,
                    timeSignature = TimeSignature(3, 4)
                )
            ),
            CueWithDuration(
                duration = CueDuration.Beats(4),
                cue = Cue(
                    bpm = 200.bpm,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
            CueWithDuration(
                duration = CueDuration.Beats(4),
                cue = Cue(
                    bpm = 100.bpm,
                    timeSignature = TimeSignature(4, 4)
                )
            ),
        ),
        loop = false,
        sounds = BuiltinClickSounds,
    ),
)

val PREVIEW_CLICK_TRACK_2 = ClickTrackWithId(
    id = 2,
    value = ClickTrack(
        name = "Preview 2",
        cues = listOf(
            CueWithDuration(
                duration = CueDuration.Beats(2),
                cue = Cue(
                    bpm = 100.bpm,
                    timeSignature = TimeSignature(3, 4)
                )
            ),
            CueWithDuration(
                duration = CueDuration.Beats(4),
                cue = Cue(
                    bpm = 120.bpm,
                    timeSignature = TimeSignature(2, 4)
                )
            ),
            CueWithDuration(
                duration = CueDuration.Beats(6),
                cue = Cue(
                    bpm = 300.bpm,
                    timeSignature = TimeSignature(5, 4)
                )
            ),
        ),
        loop = false,
        sounds = BuiltinClickSounds,
    ),
)
