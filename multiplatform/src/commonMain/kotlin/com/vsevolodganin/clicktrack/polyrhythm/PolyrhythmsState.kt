package com.vsevolodganin.clicktrack.polyrhythm

import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import kotlinx.serialization.Serializable

@Serializable
data class PolyrhythmsState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayProgress?,
)
