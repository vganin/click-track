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
    enum class SoundPriority {
        STRONG, WEAK
    }

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(Int.MAX_VALUE)
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

    fun play(sound: ClickSoundSource, priority: SoundPriority) {
        val soundId = loadedSounds.getOrPut(sound) { soundPool.load(sound) }
        soundPool.play(
            soundId,
            /* leftVolume = */ 1f,
            /* rightVolume = */ 1f,
            /* priority = */ priority.asInt(),
            /* loop = */ 0,
            /* rate = */ 1f
        )
    }

    private fun SoundPriority.asInt(): Int = when (this) {
        SoundPriority.STRONG -> 2
        SoundPriority.WEAK -> 1
    }

    fun release() {
        soundPool.release()
    }

    private fun SoundPool.load(sound: ClickSoundSource): Int {
        return when (sound) {
            ClickSoundSource.BuiltinStrong -> load(context, R.raw.strong, 1)
            ClickSoundSource.BuiltinWeak -> load(context, R.raw.weak, 1)
            is ClickSoundSource.File -> load(sound.path, 1)
        }
    }
}
