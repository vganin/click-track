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
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Thread-unsafe, should be accessed from a single thread (see [com.vsevolodganin.clicktrack.di.module.PlayerDispatcher]).
 */
@PlayerServiceScope
class PlayerSoundPool @Inject constructor(
    application: Application,
    private val soundBank: SoundBank,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) {
    private val loadedSounds = mutableMapOf<ClickSoundSource, AudioTrack>()

    init {
        // Workaround for some Android P not properly disconnecting streams, so resetting them manually
        // Source: https://github.com/google/oboe/blob/master/docs/notes/disconnect.md#workaround-for-not-disconnecting-properly
        // Since our scope is singleton, no need to unregister receiver
        application.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                GlobalScope.launch(playerDispatcher) {
                    loadedSounds.forEach { (_, track) ->
                        track.resetStream()
                    }
                }
            }
        }, IntentFilter(AudioManager.ACTION_HEADSET_PLUG))
    }

    fun warmup(soundSource: ClickSoundSource) {
        audioTrack(soundSource)?.warmup()
    }

    fun play(soundSource: ClickSoundSource) {
        audioTrack(soundSource)?.play()
    }

    fun stopAll() {
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
