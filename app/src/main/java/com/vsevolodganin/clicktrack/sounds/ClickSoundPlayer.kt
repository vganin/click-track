package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ClickSoundPlayer @Inject constructor(
    private val soundPool: PlayerSoundPool,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) {
    suspend fun play(sound: ClickSoundSource) = withContext(playerDispatcher) {
        soundPool.play(soundSource = sound)
    }
}
