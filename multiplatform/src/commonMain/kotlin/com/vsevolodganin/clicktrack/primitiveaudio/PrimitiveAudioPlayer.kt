package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.player.PlayerEvent
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import kotlin.time.Duration

expect class PrimitiveAudioPlayer {

    suspend fun play(
        startingAt: Duration,
        singleIterationDuration: Duration,
        playerEvents: Sequence<PlayerEvent>,
        reportProgress: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    )

    fun getLatencyMs(): Int
}
