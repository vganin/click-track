package net.ganin.vsevolod.clicktrack.view

import net.ganin.vsevolod.clicktrack.lib.*

val PREVIEW_CLICK_TRACK = ClickTrack(
    cues = listOf(
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(
                bpm = 100,
                timeSignature = TimeSignature(3, 4)
            )
        ),
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(
                bpm = 150,
                timeSignature = TimeSignature(3, 4)
            )
        ),
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(
                bpm = 200,
                timeSignature = TimeSignature(4, 4)
            )
        ),
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(
                bpm = 100,
                timeSignature = TimeSignature(4, 4)
            )
        ),
    ),
    loop = false
)