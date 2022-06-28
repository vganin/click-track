package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm

val PREVIEW_CLICK_TRACK_1 = ClickTrackWithDatabaseId(
    id = ClickTrackId.Database(1),
    value = ClickTrack(
        name = "Preview 1",
        cues = listOf(
            Cue(
                bpm = 60.bpm,
                timeSignature = TimeSignature(3, 4),
                duration = CueDuration.Beats(4),
            ),
            Cue(
                bpm = 150.bpm,
                timeSignature = TimeSignature(3, 4),
                duration = CueDuration.Beats(4),
            ),
            Cue(
                bpm = 200.bpm,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Beats(4),
            ),
            Cue(
                bpm = 100.bpm,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Beats(4),
            ),
        ),
        loop = false,
    ),
)

val PREVIEW_CLICK_TRACK_2 = ClickTrackWithDatabaseId(
    id = ClickTrackId.Database(2),
    value = ClickTrack(
        name = "Preview 2",
        cues = listOf(
            Cue(
                bpm = 100.bpm,
                timeSignature = TimeSignature(3, 4),
                duration = CueDuration.Beats(2),
            ),
            Cue(
                bpm = 120.bpm,
                timeSignature = TimeSignature(2, 4),
                duration = CueDuration.Beats(4),
            ),
            Cue(
                bpm = 300.bpm,
                timeSignature = TimeSignature(5, 4),
                duration = CueDuration.Beats(6),
            ),
        ),
        loop = false,
    ),
)
