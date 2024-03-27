package com.vsevolodganin.clicktrack.polyrhythm

import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

@Parcelize
data class PolyrhythmsState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayProgress?,
) : Parcelable
