package com.vsevolodganin.clicktrack.player

import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.util.Log
import androidx.media.AudioAttributesCompat
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.ApplicationContext
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundPriority
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.utils.android.media.SoundPoolSuspendingLoadWait
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@ApplicationScoped
class PlayerSoundPool @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolver: ContentResolver,
) {
    companion object {
        val AUDIO_ATTRIBUTES: AudioAttributesCompat = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_SONIFICATION)
            .build()
    }

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(Int.MAX_VALUE)
        .setAudioAttributes(AUDIO_ATTRIBUTES.unwrap() as AudioAttributes)
        .build()

    private val awaitLoad = SoundPoolSuspendingLoadWait(soundPool)

    private val loadedSounds = mutableMapOf<ClickSoundSource, Int>()

    private val mutex = Mutex()

    suspend fun preload(sounds: Iterable<ClickSoundSource>) = coroutineScope {
        awaitAll(*sounds.map { async { awaitLoad(it) } }.toTypedArray())
    }

    suspend fun play(sound: ClickSoundSource, priority: ClickSoundPriority) {
        val soundId = awaitLoad(sound) ?: return

        mutex.withLock {
            soundPool.play(
                soundId,
                /* leftVolume = */ 1f,
                /* rightVolume = */ 1f,
                /* priority = */ priority.asInt(),
                /* loop = */ 0,
                /* rate = */ 1f
            )
        }
    }

    private suspend fun awaitLoad(sound: ClickSoundSource): Int? = mutex.withLock {
        var shouldAwaitLoad = false
        val soundId = loadedSounds.getOrPut(sound) {
            shouldAwaitLoad = true
            soundPool.load(sound) ?: return null
        }

        if (shouldAwaitLoad) {
            awaitLoad(soundId)
        }

        return soundId
    }

    private fun ClickSoundPriority.asInt(): Int = when (this) {
        ClickSoundPriority.STRONG -> 2
        ClickSoundPriority.WEAK -> 1
    }

    private fun SoundPool.load(sound: ClickSoundSource): Int? {
        return when (sound) {
            is ClickSoundSource.Bundled -> load(context, sound.resId, 1)
            is ClickSoundSource.Uri -> loadUri(sound.value, 1)
        }
    }

    private fun SoundPool.loadUri(uri: String, priority: Int): Int? {
        return try {
            contentResolver.openAssetFileDescriptor(Uri.parse(uri), "r")?.use { assetFileDescriptor ->
                val fileDescriptor = assetFileDescriptor.fileDescriptor
                load(fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length, priority)
            }
        } catch (e: IOException) {
            Log.e("PlayerSoundPool", "Failed to open uri: $uri")
            null
        }
    }
}
