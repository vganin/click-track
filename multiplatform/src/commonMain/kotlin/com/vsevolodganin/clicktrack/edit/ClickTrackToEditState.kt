package com.vsevolodganin.clicktrack.edit

import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.DefaultBeatsDuration
import com.vsevolodganin.clicktrack.model.DefaultMeasuresDuration
import com.vsevolodganin.clicktrack.model.DefaultTimeDuration
import com.vsevolodganin.clicktrack.utils.optionalCast

fun ClickTrackWithDatabaseId.toEditState(showForwardButton: Boolean) = EditClickTrackState(
    id = id,
    name = value.name,
    loop = value.loop,
    tempoOffset = value.tempoOffset,
    cues = value.cues.mapIndexed { index, cue -> cue.toEditState(index) },
    showForwardButton = showForwardButton,
)

fun Cue.toEditState(index: Int) = EditCueState(
    name = name.orEmpty(),
    displayPosition = (index + 1).toString(),
    bpm = bpm.value,
    timeSignature = timeSignature,
    activeDurationType = duration.type,
    beats = duration.optionalCast<CueDuration.Beats>() ?: DefaultBeatsDuration,
    measures = duration.optionalCast<CueDuration.Measures>() ?: DefaultMeasuresDuration,
    time = duration.optionalCast<CueDuration.Time>() ?: DefaultTimeDuration,
    pattern = pattern,
    errors = emptySet(),
)
