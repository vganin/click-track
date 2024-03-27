package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize

@Parcelize
data class PlaybackState(
    val id: PlayableId,
    val progress: PlayProgress,
) : Parcelable
