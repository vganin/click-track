package com.vsevolodganin.clicktrack.play

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.PlayProgress

@Parcelize
data class PlayClickTrackState(
    val clickTrack: ClickTrackWithDatabaseId,
    val playProgress: PlayProgress?,
    val playTrackingMode: Boolean,
) : Parcelable

val PlayClickTrackState.isPlaying: Boolean get() = playProgress != null
val PlayClickTrackState.isPaused: Boolean get() = playProgress?.isPaused == true
