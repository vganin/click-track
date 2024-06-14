package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import kotlinx.coroutines.flow.Flow

interface PlayerServiceAccess {
    fun start(id: PlayableId, atProgress: Double? = null, soundsId: ClickSoundsId? = null)

    fun pause()

    fun resume()

    fun stop()

    fun playbackState(): Flow<PlaybackState?>
}
