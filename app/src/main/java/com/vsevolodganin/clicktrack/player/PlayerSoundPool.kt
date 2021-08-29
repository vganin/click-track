package com.vsevolodganin.clicktrack.player

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import javax.inject.Inject
import timber.log.Timber

/**
 * Thread-unsafe, should be accessed from a single thread (see [com.vsevolodganin.clicktrack.di.module.PlayerDispatcher]).
 */
@ApplicationScoped
class PlayerSoundPool @Inject constructor(
    private val context: Context,
    private val audioDecoder: AudioDecoder,
    private val contentResolver: ContentResolver,
) {
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

        audioTrack.play()
    }

    fun stop() {
        loadedSounds.values.forEach { audioTrack ->
            audioTrack.stop()
        }
    }

    fun stopAll() {
        loadedSounds.values.forEach { audioTrack ->
            audioTrack.stop()
        }
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
        val decodingResult = audioDecoder.decodeAudioTrack(afd, Const.MAX_SECONDS)

        return AudioTrack(
            data = decodingResult.data,
            channelCount = decodingResult.channelCount,
            pcmEncoding = decodingResult.pcmEncoding,
            sampleRate = decodingResult.sampleRate,
        )
    }

    private object Const {
        const val MAX_SECONDS = 2
    }
}
