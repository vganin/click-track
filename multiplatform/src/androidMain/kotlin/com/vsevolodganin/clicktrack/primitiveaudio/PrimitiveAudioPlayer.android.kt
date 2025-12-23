package com.vsevolodganin.clicktrack.primitiveaudio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.player.PlayerEvent
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import com.vsevolodganin.clicktrack.utils.log.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.yield
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Inject
@SingleIn(PlayerServiceScope::class)
actual class PrimitiveAudioPlayer(
    primitiveAudioMonoRendererFactory: PrimitiveAudioMonoRenderer.Factory,
    audioManager: AudioManager,
    private val logger: Logger,
) {
    private val audioSessionId: Int = audioManager.generateAudioSessionId()

    private val audioTrack: AudioTrack = AudioTrack(
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build(),
        AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build(),
        BUFFER_LENGTH_IN_FRAMES * Float.SIZE_BYTES,
        AudioTrack.MODE_STREAM,
        audioSessionId,
    )

    private val primitiveAudioMonoRenderer = primitiveAudioMonoRendererFactory.create(SAMPLE_RATE)

    actual suspend fun play(
        startingAt: Duration,
        singleIterationDuration: Duration,
        playerEvents: Sequence<PlayerEvent>,
        reportProgress: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    ) {
        try {
            audioTrack.play()

            reportProgress(startingAt)

            val samplesNumber = convertDurationToFramesNumber(singleIterationDuration)
            val samplesChunks = primitiveAudioMonoRenderer.renderToMonoSamples(playerEvents, soundSourceProvider)
                .chunked(BUFFER_LENGTH_IN_FRAMES, List<Float>::toFloatArray)

            audioTrack.notificationMarkerPosition = samplesNumber - convertDurationToFramesNumber(startingAt)
            audioTrack.setPlaybackPositionUpdateListener(
                object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(track: AudioTrack) {
                        reportProgress(Duration.ZERO)
                        audioTrack.notificationMarkerPosition += samplesNumber
                    }

                    override fun onPeriodicNotification(track: AudioTrack) = Unit
                },
            )

            for (samplesChunk in samplesChunks) {
                var samplesWritten = 0
                while (samplesWritten < samplesChunk.size) {
                    yield()

                    val result = audioTrack.write(
                        samplesChunk,
                        samplesWritten,
                        samplesChunk.size - samplesWritten,
                        AudioTrack.WRITE_NON_BLOCKING,
                    )

                    if (result == 0) {
                        delay(BUFFER_LENGTH_IN_SECONDS.seconds / 2)
                    } else if (result > 0) {
                        yield()
                        samplesWritten += result
                    } else {
                        logger.logError(TAG, "Got unexpected result: $result")
                        return
                    }
                }
            }

            suspendCancellableCoroutine { continuation ->
                audioTrack.setPlaybackPositionUpdateListener(
                    object : AudioTrack.OnPlaybackPositionUpdateListener {
                        override fun onMarkerReached(track: AudioTrack) = continuation.resume(Unit)
                        override fun onPeriodicNotification(track: AudioTrack) = Unit
                    },
                )
                if (audioTrack.playbackHeadPosition >= audioTrack.notificationMarkerPosition) {
                    continuation.resume(Unit)
                }
            }
        } finally {
            audioTrack.setPlaybackPositionUpdateListener(null)
            audioTrack.pause()
            audioTrack.flush()
        }
    }

    actual fun getLatencyMs(): Int {
        try {
            val getLatencyMethod = AudioTrack::class.java.getMethod("getLatency")
            return getLatencyMethod.invoke(audioTrack) as Int - BUFFER_LENGTH_IN_SECONDS.seconds.inWholeMilliseconds.toInt()
        } catch (throwable: Throwable) {
            logger.logError(TAG, "Failed to get latency using getLatency method", throwable)
        }
        return 0
    }

    private fun convertDurationToFramesNumber(duration: Duration): Int {
        return convertDurationToFramesNumber(duration, SAMPLE_RATE, 1)
    }

    private companion object {
        const val TAG = "PrimitiveAudioPlayer"

        const val SAMPLE_RATE = 44100
        const val BUFFER_LENGTH_IN_SECONDS = 2
        const val BUFFER_LENGTH_IN_FRAMES = SAMPLE_RATE * BUFFER_LENGTH_IN_SECONDS
    }
}
