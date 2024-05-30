package com.vsevolodganin.clicktrack.training

import com.vsevolodganin.clicktrack.model.CueDuration
import kotlinx.coroutines.flow.StateFlow

interface TrainingViewModel {
    val state: StateFlow<TrainingEditState>

    fun onBackClick()

    fun onAcceptClick()

    fun onStartingTempoChange(startingTempo: Int)

    fun onModeSelect(mode: TrainingEditState.TrainingMode)

    fun onSegmentLengthChange(segmentLength: CueDuration)

    fun onSegmentLengthTypeChange(segmentLengthType: CueDuration.Type)

    fun onTempoChangeChange(tempoChange: Int)

    fun onEndingChange(ending: TrainingEditState.Ending)

    fun onEndingKindChange(endingKind: TrainingEndingKind)
}
