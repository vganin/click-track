package com.vsevolodganin.clicktrack.primitiveaudio

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
class PrimitiveAudioProvider(
    private val application: Application,
    private val audioDecoder: PrimitiveAudioExtractor,
    private val contentResolver: ContentResolver,
) {
    fun get(sound: ClickSoundSource): PrimitiveAudioData? {
        return try {
            load(sound)
        } catch (t: Throwable) {
            Timber.e("Failed to load $sound", t)
            null
        }
    }

    private fun load(sound: ClickSoundSource): PrimitiveAudioData? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(sound.audioResource.rawResId)
            is ClickSoundSource.Uri -> load(sound.value)
        }
    }

    private fun load(@RawRes resId: Int): PrimitiveAudioData? {
        return application.resources.openRawResourceFd(resId).use(::load)
    }

    private fun load(uri: String): PrimitiveAudioData? {
        return contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use(::load)
    }

    private fun load(afd: AssetFileDescriptor): PrimitiveAudioData? {
        return audioDecoder.extractPcm(afd, MAX_SECONDS)
    }

    private companion object Const {
        const val MAX_SECONDS = 2
    }
}
