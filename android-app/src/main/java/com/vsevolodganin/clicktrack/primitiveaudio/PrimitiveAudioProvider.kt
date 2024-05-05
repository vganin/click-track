package com.vsevolodganin.clicktrack.primitiveaudio

import android.app.Application
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.utils.log.Logger
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class PrimitiveAudioProvider(
    private val application: Application,
    private val audioDecoder: PrimitiveAudioExtractor,
    private val contentResolver: ContentResolver,
    private val logger: Logger,
) {
    fun get(sound: ClickSoundSource): PrimitiveFloatAudioData? {
        return try {
            load(sound)?.let(PrimitiveFloatAudioData::from)
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to load $sound", t)
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
        return audioDecoder.extract(afd, MAX_SECONDS)
    }

    private companion object Const {
        const val TAG = "PrimitiveAudioProvider"
        const val MAX_SECONDS = 2
    }
}
