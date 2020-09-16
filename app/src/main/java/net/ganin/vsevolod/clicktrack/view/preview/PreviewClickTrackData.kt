package net.ganin.vsevolod.clicktrack.view.preview

import net.ganin.vsevolod.clicktrack.lib.*

val PREVIEW_CLICK_TRACK_1 = ClickTrack(
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

val PREVIEW_CLICK_TRACK_2 = ClickTrack(
    cues = listOf(
        CueWithDuration(
            duration = CueDuration.Beats(2),
            cue = Cue(
                bpm = 100,
                timeSignature = TimeSignature(3, 4)
            )
        ),
        CueWithDuration(
            duration = CueDuration.Beats(4),
            cue = Cue(
                bpm = 120,
                timeSignature = TimeSignature(2, 4)
            )
        ),
        CueWithDuration(
            duration = CueDuration.Beats(6),
            cue = Cue(
                bpm = 300,
                timeSignature = TimeSignature(5, 4)
            )
        ),
    ),
    loop = false
)
