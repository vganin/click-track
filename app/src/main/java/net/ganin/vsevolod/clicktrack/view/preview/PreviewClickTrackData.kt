package net.ganin.vsevolod.clicktrack.view.preview

import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm

val PREVIEW_CLICK_TRACK_1 = ClickTrack(
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
    loop = false
)

val PREVIEW_CLICK_TRACK_2 = ClickTrack(
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
    loop = false
)
