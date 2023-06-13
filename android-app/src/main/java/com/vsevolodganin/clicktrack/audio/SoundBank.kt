package com.vsevolodganin.clicktrack.audio

import android.app.Application
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import me.tatarka.inject.annotations.Inject
import timber.log.Timber

@ApplicationScope
@Inject
class SoundBank(
    private val application: Application,
    private val pcmExtractor: PcmExtractor,
    private val contentResolver: ContentResolver,
) {
    private val lock = Any()
    private val cache = mutableMapOf<ClickSoundSource, PcmData>()

    fun get(sound: ClickSoundSource): PcmData? = synchronized(lock) {
        return cache.getOrPut(sound) {
            try {
                load(sound) ?: return null
            } catch (t: Throwable) {
                Timber.e("Failed to load $sound", t)
                return null
            }
        }
    }

    private fun load(sound: ClickSoundSource): PcmData? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(sound.audioResource.rawResId)
            is ClickSoundSource.Uri -> load(sound.value)
        }
    }

    private fun load(@RawRes resId: Int): PcmData? {
        return application.resources.openRawResourceFd(resId).use(::load)
    }

    private fun load(uri: String): PcmData? {
        return contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use(::load)
    }

    private fun load(afd: AssetFileDescriptor): PcmData? {
        return pcmExtractor.extractPcm(afd, MAX_SECONDS)
    }

    private companion object Const {
        const val MAX_SECONDS = 2
    }
}
