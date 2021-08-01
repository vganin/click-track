package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.state.redux.TrainingMode
import com.vsevolodganin.clicktrack.state.redux.TrainingState

data class TrainingUiState(
    val startingTempo: Int,
    val mode: TrainingMode,
    val segmentLength: CueDuration,
    val tempoChange: Int,
    val ending: TrainingState.Ending,
    val errors: Set<TrainingState.Error>,
)
