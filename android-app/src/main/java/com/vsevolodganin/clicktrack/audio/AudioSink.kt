package com.vsevolodganin.clicktrack.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Process
import com.vsevolodganin.clicktrack.utils.coroutine.createSingleThreadCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.resume

@Inject
class AudioSink(audioManager: AudioManager) {

    companion object {
        const val CHANNEL_MASK = AudioFormat.CHANNEL_OUT_MONO
        const val PCM_ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val SAMPLE_RATE = 44100
    }

    private val audioWriterThreadDispatcher = createSingleThreadCoroutineDispatcher(
        "ClickTrackAudioWriter",
        Process.THREAD_PRIORITY_URGENT_AUDIO
    )

    private val sessionId = audioManager.generateAudioSessionId()
    private var track = createTrack()

    private var isPaused = MutableStateFlow(true)

    fun play() {
        isPaused.value = false
        track.play()
    }

    fun stop() {
        isPaused.value = true
        // Using pause() + flush() for immediate stop. See stop() documentation for explanation
        track.apply {
            pause()
            flush()
        }
    }

    fun pause() {
        isPaused.value = true
        track.pause()
    }

    suspend fun write(audioData: ByteArray) = withContext(audioWriterThreadDispatcher) {
        suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { pause() }

            var written = 0
            while (continuation.isActive && written < audioData.size) {
                val result = track.write(audioData, 0, audioData.size)

                if (!continuation.isActive) {
                    break
                }

                if (result >= 0) {
                    written += result
                    runBlocking {
                        isPaused.first { !it }
                    }
                } else if (result == AudioTrack.ERROR_DEAD_OBJECT) {
                    track = createTrack()
                } else {
                    throw RuntimeException("Fatal error writing data to audio sink")
                }
            }

            continuation.resume(Unit)
        }
    }

    private fun createTrack(): AudioTrack {
        return AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(PCM_ENCODING)
                .setChannelMask(CHANNEL_MASK)
                .build(),
            AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, PCM_ENCODING),
            AudioTrack.MODE_STREAM,
            sessionId
        )
    }
}
