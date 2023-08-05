package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import kotlinx.coroutines.flow.Flow

interface Player {
    val playbackState: Flow<PlaybackState?>

    fun setPlayable(id: PlayableId)
    fun setSounds(id: ClickSoundsId?)
    fun setProgress(progress: Double)
    fun play()
    fun stop()
    fun pause()
}

fun Player.play(id: PlayableId) {
    setPlayable(id)
    play()
}
