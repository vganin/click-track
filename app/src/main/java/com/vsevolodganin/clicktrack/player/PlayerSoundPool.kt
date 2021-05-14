package com.vsevolodganin.clicktrack.player

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media.AudioAttributesCompat
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import javax.inject.Inject
import timber.log.Timber

/**
 * Thread-unsafe, should be accessed from a single thread.
 */
@ApplicationScoped
class PlayerSoundPool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioDecoder: AudioDecoder,
    private val contentResolver: ContentResolver,
    audioManager: AudioManager,
) {
    companion object {
        val AUDIO_ATTRIBUTES: AudioAttributesCompat = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_SONIFICATION)
            .build()
    }

    private val sessionId = audioManager.generateAudioSessionId()

    private val loadedSounds = mutableMapOf<ClickSoundSource, AudioTrack>()

    fun preload(sounds: Iterable<ClickSoundSource>) {
        loadedSounds += sounds
            .mapNotNull { soundSource ->
                val audioTrack = load(soundSource) ?: return@mapNotNull null
                soundSource to audioTrack
            }
            .toMap()
    }

    fun play(soundSource: ClickSoundSource) {
        val audioTrack = loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return
        }

        audioTrack.stop()
        audioTrack.play()
    }

    private fun load(sound: ClickSoundSource): AudioTrack? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(sound.resId)
            is ClickSoundSource.Uri -> load(sound.value)
        }
    }

    private fun load(@RawRes resId: Int): AudioTrack? {
        return try {
            context.resources.openRawResourceFd(resId).use(::load)
        } catch (e: Throwable) {
            Timber.e(e, "Failed to load raw resource: $resId")
            null
        }
    }

    private fun load(uri: String): AudioTrack? {
        return try {
            contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use(::load)
        } catch (e: Throwable) {
            Timber.e(e, "Failed to load uri: $uri")
            null
        }
    }

    private fun load(afd: AssetFileDescriptor): AudioTrack {
        val decodingResult = audioDecoder.decodeAudioTrack(afd, Const.MAX_PCM_16BIT_MONO_LENGTH)

        return AudioTrack(
            AUDIO_ATTRIBUTES.unwrap() as AudioAttributes,
            AudioFormat.Builder()
                .setSampleRate(decodingResult.sampleRate)
                .setEncoding(decodingResult.audioFormat)
                .setChannelMask(decodingResult.channelMask)
                .build(),
            decodingResult.bytes.size,
            AudioTrack.MODE_STATIC,
            sessionId,
        ).apply {
            write(decodingResult.bytes, 0, decodingResult.bytes.size)
        }
    }

    private object Const {
        const val MAX_PCM_16BIT_MONO_LENGTH = 2 * 2 * 44100 // max 2 seconds of PCM 16 BIT 44100 Hz
    }
}
