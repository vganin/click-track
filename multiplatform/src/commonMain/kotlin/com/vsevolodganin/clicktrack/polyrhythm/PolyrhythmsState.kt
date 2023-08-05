package com.vsevolodganin.clicktrack.polyrhythm

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.ui.piece.PlayProgress

@Parcelize
data class PolyrhythmsState(
    val twoLayerPolyrhythm: TwoLayerPolyrhythm,
    val isPlaying: Boolean,
    val playableProgress: PlayProgress?,
) : Parcelable
