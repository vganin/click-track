package com.vsevolodganin.clicktrack.player

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.lib.ClickSoundSource
import javax.inject.Inject

class PlayerSoundPool @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_UNKNOWN)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
            .build())
        .build()

    private val loadedSounds = mutableMapOf(
        ClickSoundSource.BuiltinStrong to soundPool.load(ClickSoundSource.BuiltinStrong),
        ClickSoundSource.BuiltinWeak to soundPool.load(ClickSoundSource.BuiltinWeak),
    )

    fun play(sound: ClickSoundSource) {
        val soundId = loadedSounds.getOrPut(sound) { soundPool.load(sound) }
        soundPool.play(
            soundId,
            /* leftVolume = */ 1f,
            /* rightVolume = */ 1f,
            /* priority = */ Const.DEFAULT_PRIORITY,
            /* loop = */ 0,
            /* rate = */ 1f
        )
    }

    fun release() {
        soundPool.release()
    }

    private fun SoundPool.load(sound: ClickSoundSource): Int {
        return when (sound) {
            ClickSoundSource.BuiltinStrong -> load(context, R.raw.strong, Const.DEFAULT_PRIORITY)
            ClickSoundSource.BuiltinWeak -> load(context, R.raw.weak, Const.DEFAULT_PRIORITY)
            is ClickSoundSource.File -> load(sound.path, Const.DEFAULT_PRIORITY)
        }
    }

    private object Const {
        const val DEFAULT_PRIORITY = 1
    }
}
