package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm

data class PolyrhythmsUiState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayProgress?,
)
