package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import kotlinx.serialization.Serializable

@Serializable
data class PlaybackState(
    val id: PlayableId,
    val progress: PlayProgress,
)
