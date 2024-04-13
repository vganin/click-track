package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.primitiveaudio.PrimitiveAudioPlayer
import com.vsevolodganin.clicktrack.primitiveaudio.PrimitiveAudioProvider
import me.tatarka.inject.annotations.Inject

@PlayerServiceScope
@Inject
class PlayerSoundPool(
    private val soundBank: PrimitiveAudioProvider,
    private val pcmPlayer: PrimitiveAudioPlayer,
) {
    private val loadedSounds = mutableMapOf<ClickSoundSource, Int>()

    fun play(soundSource: ClickSoundSource) {
        pcmPlayer.play(playerIndex(soundSource) ?: return)
    }

    fun stopAll() {
        loadedSounds.values.forEach { index ->
            pcmPlayer.stop(index)
        }
    }

    private fun playerIndex(soundSource: ClickSoundSource): Int? {
        return loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return null
        }
    }

    private fun load(sound: ClickSoundSource): Int? {
        return soundBank.get(sound)?.let(pcmPlayer::loadAndGetIndex)
    }
}
