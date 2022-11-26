package com.vsevolodganin.clicktrack.polyrhythm

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import kotlinx.parcelize.Parcelize

@Parcelize
data class PolyrhythmsState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayProgress?,
) : Parcelable
