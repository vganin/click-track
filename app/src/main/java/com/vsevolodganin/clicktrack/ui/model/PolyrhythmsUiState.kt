package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.lib.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.PlayableProgress

data class PolyrhythmsUiState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayableProgress?,
)
