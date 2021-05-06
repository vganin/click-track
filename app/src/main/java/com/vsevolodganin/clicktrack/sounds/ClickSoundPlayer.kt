package com.vsevolodganin.clicktrack.sounds

import com.vsevolodganin.clicktrack.player.PlayerSoundPool
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import javax.inject.Inject

class ClickSoundPlayer @Inject constructor(
    private val soundPool: PlayerSoundPool
) {
    suspend fun play(sound: ClickSoundSource) {
        soundPool.play(sound = sound, type = ClickSoundType.STRONG)
    }
}
