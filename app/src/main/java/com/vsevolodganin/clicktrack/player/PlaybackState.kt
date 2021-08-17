package com.vsevolodganin.clicktrack.player

import android.os.Parcelable
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.PlayableProgress
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaybackState(
    val id: PlayableId,
    val progress: PlayableProgress,
) : Parcelable
