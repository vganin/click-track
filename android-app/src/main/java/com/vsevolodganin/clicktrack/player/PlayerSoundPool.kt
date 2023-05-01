package com.vsevolodganin.clicktrack.player

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFormat
import android.media.AudioManager
import com.vsevolodganin.clicktrack.audio.AudioTrack
import com.vsevolodganin.clicktrack.audio.SoundBank
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import me.tatarka.inject.annotations.Inject

@PlayerServiceScope
@Inject
class PlayerSoundPool(
    application: Application,
    private val soundBank: SoundBank,
) {
    private val lock = Any()
    private val loadedSounds = mutableMapOf<ClickSoundSource, AudioTrack>()

    init {
        // Workaround for some Android P not properly disconnecting streams, so resetting them manually
        // Source: https://github.com/google/oboe/blob/master/docs/notes/disconnect.md#workaround-for-not-disconnecting-properly
        // Since our scope is singleton, no need to unregister receiver
        application.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                synchronized(lock) {
                    loadedSounds.forEach { (_, track) ->
                        track.recover()
                    }
                }
            }
        }, IntentFilter(AudioManager.ACTION_HEADSET_PLUG))
    }

    fun warmup(soundSource: ClickSoundSource) = synchronized(lock) {
        audioTrack(soundSource)?.warmup()
    }

    fun play(soundSource: ClickSoundSource) = synchronized(lock) {
        audioTrack(soundSource)?.play()
    }

    fun stopAll() = synchronized(lock) {
        loadedSounds.values.forEach { audioTrack ->
            audioTrack.stop()
        }
    }

    private fun audioTrack(soundSource: ClickSoundSource): AudioTrack? {
        return loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return null
        }
    }

    private fun load(sound: ClickSoundSource): AudioTrack? {
        return soundBank.get(sound)?.run {
            AudioTrack(
                data = data,
                pcmEncoding = AudioFormat.ENCODING_PCM_16BIT,
                channelCount = channelCount,
                sampleRate = sampleRate,
            )
        }
    }
}
