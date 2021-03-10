package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundPriority
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import javax.inject.Inject

class ClickSoundPlayer @Inject constructor(
    private val soundPool: PlayerSoundPool
) {
    suspend fun play(sound: ClickSoundSource) {
        soundPool.play(sound = sound, priority = ClickSoundPriority.STRONG)
    }
}
