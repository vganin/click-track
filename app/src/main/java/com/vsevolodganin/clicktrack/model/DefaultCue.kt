package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm

val DefaultCue = Cue(
    name = null,
    bpm = 120.bpm,
    timeSignature = TimeSignature(4, 4),
    duration = CueDuration.Measures(1),
)
