package com.vsevolodganin.clicktrack.player

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaybackState(
    val id: PlayableId,
    val progress: PlayProgress,
) : Parcelable
