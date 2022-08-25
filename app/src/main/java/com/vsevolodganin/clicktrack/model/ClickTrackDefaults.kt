package com.vsevolodganin.clicktrack.model

import kotlin.time.Duration.Companion.minutes

val DefaultBeatsDuration = CueDuration.Beats(4)
val DefaultMeasuresDuration = CueDuration.Measures(1)
val DefaultTimeDuration = CueDuration.Time(1.minutes)

val DefaultCue = Cue(
    name = null,
    bpm = 120.bpm,
    timeSignature = TimeSignature(4, 4),
    duration = DefaultMeasuresDuration,
)
