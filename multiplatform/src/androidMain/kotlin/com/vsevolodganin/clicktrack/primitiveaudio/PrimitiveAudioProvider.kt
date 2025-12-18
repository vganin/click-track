package com.vsevolodganin.clicktrack.primitiveaudio

import android.app.Application
import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import androidx.core.net.toUri
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.utils.log.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ApplicationScope::class)
@Inject
class PrimitiveAudioProvider(
    private val application: Application,
    private val primitiveAudioExtractor: PrimitiveAudioExtractor,
    private val contentResolver: ContentResolver,
    private val logger: Logger,
) {
    fun get(sound: ClickSoundSource): PrimitiveFloatAudioData? {
        return try {
            load(sound.uri)?.let(PrimitiveFloatAudioData::from)
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to load $sound", t)
            null
        }
    }

    private fun load(uri: String): PrimitiveAudioData? {
        return openAssetFileDescriptor(uri)?.use(::load)
    }

    private fun load(afd: AssetFileDescriptor): PrimitiveAudioData? {
        return primitiveAudioExtractor.extract(afd, MAX_SECONDS)
    }

    private fun openAssetFileDescriptor(uri: String): AssetFileDescriptor? {
        return if (uri.startsWith(ASSET_PATH_PREFIX)) {
            application.assets.openFd(uri.removePrefix(ASSET_PATH_PREFIX))
        } else {
            contentResolver.openAssetFileDescriptor(uri.toUri(), "r")
        }
    }

    private companion object Const {
        const val TAG = "PrimitiveAudioProvider"
        const val MAX_SECONDS = 2
        const val ASSET_PATH_PREFIX = "file:///android_asset/"
    }
}
