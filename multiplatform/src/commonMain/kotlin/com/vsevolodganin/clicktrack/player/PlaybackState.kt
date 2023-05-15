package com.vsevolodganin.clicktrack.player

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId

@Parcelize
data class PlaybackState(
    val id: PlayableId,
    val progress: PlayProgress,
) : Parcelable
