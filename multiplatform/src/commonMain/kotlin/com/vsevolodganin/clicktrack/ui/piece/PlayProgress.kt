package com.vsevolodganin.clicktrack.ui.piece

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.player.PlaybackPosition
import com.vsevolodganin.clicktrack.player.PlaybackState
import kotlin.time.Duration

@Parcelize
data class PlayProgress(
    val isPaused: Boolean,
    val position: PlaybackPosition
) : Parcelable {
    val realPosition: Duration get() = if (isPaused) position.value else position.value + position.emissionTime.elapsedNow()
}

fun PlaybackState.toPlayProgress() = PlayProgress(
    position = position,
    isPaused = isPaused
)
