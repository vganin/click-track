package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import kotlin.time.Duration

val DefaultBeatsDuration = CueDuration.Beats(4)
val DefaultMeasuresDuration = CueDuration.Measures(1)
val DefaultTimeDuration = CueDuration.Time(Duration.minutes(1))

val DefaultCue = Cue(
    name = null,
    bpm = 120.bpm,
    timeSignature = TimeSignature(4, 4),
    duration = DefaultMeasuresDuration,
)
