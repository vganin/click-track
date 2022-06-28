package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.redux.TrainingMode
import com.vsevolodganin.clicktrack.redux.TrainingState

data class TrainingUiState(
    val startingTempo: Int,
    val mode: TrainingMode,
    val segmentLength: CueDuration,
    val tempoChange: Int,
    val ending: TrainingState.Ending,
    val errors: Set<TrainingState.Error>,
)
