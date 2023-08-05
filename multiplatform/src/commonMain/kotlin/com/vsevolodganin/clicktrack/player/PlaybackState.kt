package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.PlayableId

data class PlaybackState(
    val id: PlayableId,
    val isPaused: Boolean,
    val position: PlaybackPosition,
)
