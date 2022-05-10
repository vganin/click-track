package com.vsevolodganin.clicktrack.player

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.net.Uri
import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Thread-unsafe, should be accessed from a single thread (see [com.vsevolodganin.clicktrack.di.module.PlayerDispatcher]).
 */
@ApplicationScoped
class PlayerSoundPool @Inject constructor(
    private val context: Context,
    private val audioDecoder: AudioDecoder,
    private val contentResolver: ContentResolver,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) {
    private val loadedSounds = mutableMapOf<ClickSoundSource, AudioTrack>()

    init {
        // Workaround for some Android P not properly disconnecting streams, so resetting them manually
        // Source: https://github.com/google/oboe/blob/master/docs/notes/disconnect.md#workaround-for-not-disconnecting-properly
        // Since our scope is singleton, no need to unregister receiver
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                GlobalScope.launch(playerDispatcher) {
                    loadedSounds.forEach { (_, track) ->
                        track.resetStream()
                    }
                }
            }
        }, IntentFilter(AudioManager.ACTION_HEADSET_PLUG))
    }

    fun preload(sounds: Iterable<ClickSoundSource>) {
        loadedSounds += sounds
            .mapNotNull { soundSource ->
                val audioTrack = load(soundSource) ?: return@mapNotNull null
                soundSource to audioTrack
            }
            .toMap()
    }

    fun warmup(soundSource: ClickSoundSource) {
        val audioTrack = loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return
        }

        audioTrack.warmup()
    }

    fun play(soundSource: ClickSoundSource): Duration {
        val audioTrack = loadedSounds.getOrPut(soundSource) {
            load(soundSource) ?: return Duration.ZERO
        }

        audioTrack.play()

        return audioTrack.getLatencyMs().milliseconds
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
