package com.vsevolodganin.clicktrack.sounds

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.player.AudioDecoder
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.Pcm16Data
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundBank @Inject constructor(
    private val context: Context,
    private val audioDecoder: AudioDecoder,
    private val contentResolver: ContentResolver,
) {
    private val cache = ConcurrentHashMap<ClickSoundSource, Pcm16Data>()

    fun get(sound: ClickSoundSource): Pcm16Data? {
        return cache.getOrPut(sound) {
            load(sound) ?: return null
        }
    }

    private fun load(sound: ClickSoundSource): Pcm16Data? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(sound.resId)
            is ClickSoundSource.Uri -> load(sound.value)
        }
    }

    private fun load(@RawRes resId: Int): Pcm16Data? {
        return context.resources.openRawResourceFd(resId).use(::load)
    }

    private fun load(uri: String): Pcm16Data? {
        return contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use(::load)
    }

    private fun load(afd: AssetFileDescriptor): Pcm16Data? {
        return audioDecoder.extractPcm(afd, Const.MAX_SECONDS)
    }

    private object Const {
        const val MAX_SECONDS = 2
    }
}
