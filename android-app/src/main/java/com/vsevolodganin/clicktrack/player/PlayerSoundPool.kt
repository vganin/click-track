package com.vsevolodganin.clicktrack.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.vsevolodganin.clicktrack.audio.SoundBank
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import me.tatarka.inject.annotations.Inject

@PlayerServiceScope
@Inject
class PlayerSoundPool(
    private val soundBank: SoundBank,
) {
    private val lock = Any()
    private val loadedSounds = mutableMapOf<ClickSoundSource, AudioTrack>()

    fun play(soundSource: ClickSoundSource) = synchronized(lock) {
        audioTrack(soundSource)?.apply {
            playbackHeadPosition = 0
            play()
        }
    }

    fun stopAll() = synchronized(lock) {
        loadedSounds.values.forEach { audioTrack ->
            audioTrack.pause()
        }
    }

    private fun audioTrack(soundSource: ClickSoundSource): AudioTrack? {
        return loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return null
        }
    }

    private fun load(sound: ClickSoundSource): AudioTrack? {
        return soundBank.get(sound)?.let { pcmData ->
            AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
                AudioFormat.Builder()
                    .setSampleRate(pcmData.sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(if (pcmData.channelCount == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO)
                    .build(),
                pcmData.data.size,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            ).apply {
                write(pcmData.data, 0, pcmData.data.size)
            }
        }
    }
}
