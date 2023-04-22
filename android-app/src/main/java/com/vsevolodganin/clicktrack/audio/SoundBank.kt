package com.vsevolodganin.clicktrack.audio

import android.app.Application
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundBank @Inject constructor(
    private val application: Application,
    private val audioDecoder: AudioDecoder,
    private val contentResolver: ContentResolver,
) {
    private val lock = Any()
    private val cache = mutableMapOf<ClickSoundSource, Pcm16Data>()

    fun get(sound: ClickSoundSource): Pcm16Data? = synchronized(lock) {
        return cache.getOrPut(sound) {
            try {
                load(sound) ?: return null
            } catch (t: Throwable) {
                Timber.e("Failed to load $sound", t)
                return null
            }
        }
    }

    private fun load(sound: ClickSoundSource): Pcm16Data? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(sound.audioResource.rawResId)
            is ClickSoundSource.Uri -> load(sound.value)
        }
    }

    private fun load(@RawRes resId: Int): Pcm16Data? {
        return application.resources.openRawResourceFd(resId).use(::load)
    }

    private fun load(uri: String): Pcm16Data? {
        return contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use(::load)
    }

    private fun load(afd: AssetFileDescriptor): Pcm16Data? {
        return audioDecoder.extractPcm(afd, MAX_SECONDS)
    }

    private companion object Const {
        const val MAX_SECONDS = 2
    }
}
