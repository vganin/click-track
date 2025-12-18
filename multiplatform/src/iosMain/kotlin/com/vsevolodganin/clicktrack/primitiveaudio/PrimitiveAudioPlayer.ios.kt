package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.player.PlayerEvent
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import com.vsevolodganin.clicktrack.utils.time.rem
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.delay
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioFormat
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioPlayerNode
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.IOBufferDuration
import platform.AVFAudio.setActive
import platform.posix.memcpy
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalForeignApi::class)
@Inject
@SingleIn(PlayerServiceScope::class)
actual class PrimitiveAudioPlayer(
    primitiveAudioMonoRendererFactory: PrimitiveAudioMonoRenderer.Factory,
) {

    private val primitiveAudioMonoRenderer = primitiveAudioMonoRendererFactory.create(SAMPLE_RATE)

    private val audioSession = AVAudioSession.sharedInstance()

    private val audioEngine = AVAudioEngine()

    private val audioPlayerNode = AVAudioPlayerNode()

    private val audioFormat = AVAudioFormat(
        standardFormatWithSampleRate = SAMPLE_RATE.toDouble(),
        channels = 1u,
    )

    init {
        with(audioSession) {
            setCategory(AVAudioSessionCategoryPlayback, error = null)
            setMode(AVAudioSessionModeDefault, error = null)
        }
        with(audioEngine) {
            attachNode(audioPlayerNode)
            connect(audioPlayerNode, mainMixerNode, audioFormat)
            prepare()
        }
    }

    actual suspend fun play(
        startingAt: Duration,
        singleIterationDuration: Duration,
        playerEvents: Sequence<PlayerEvent>,
        reportProgress: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    ) {
        try {
            audioSession.setActive(true, error = null)
            audioEngine.startAndReturnError(null)
            audioPlayerNode.play()

            val singleIterationInFrames = convertDurationToFramesNumber(singleIterationDuration)
            val samplesChunks = primitiveAudioMonoRenderer.renderToMonoSamples(playerEvents, soundSourceProvider)
                .chunked(BUFFER_LENGTH_IN_FRAMES, List<Float>::toFloatArray)

            var usedBufferInSeconds = 0
            var progress = startingAt % singleIterationDuration

            reportProgress(progress)

            for (samplesChunk in samplesChunks) {
                val currentProgress = progress
                val nextProgress = currentProgress + BUFFER_LENGTH_IN_SECONDS.seconds

                if (nextProgress >= singleIterationDuration) {
                    val currentProgressInFrames = convertDurationToFramesNumber(currentProgress)
                    val framesTillIterationFinishes = singleIterationInFrames - currentProgressInFrames
                    val chunkOfPreviousIteration = samplesChunk.sliceArray(0..<framesTillIterationFinishes)
                    val chunkOfNextIteration = samplesChunk.sliceArray(framesTillIterationFinishes..<samplesChunk.size)

                    audioPlayerNode.scheduleBuffer(
                        chunkOfPreviousIteration.toAVAudioPCMBuffer(),
                        completionHandler = {
                            reportProgress(Duration.ZERO)
                        },
                    )
                    audioPlayerNode.scheduleBuffer(
                        chunkOfNextIteration.toAVAudioPCMBuffer(),
                        completionHandler = {
                            usedBufferInSeconds -= BUFFER_LENGTH_IN_SECONDS
                        },
                    )
                } else {
                    audioPlayerNode.scheduleBuffer(
                        samplesChunk.toAVAudioPCMBuffer(),
                        completionHandler = {
                            usedBufferInSeconds -= BUFFER_LENGTH_IN_SECONDS
                        },
                    )
                }

                usedBufferInSeconds += BUFFER_LENGTH_IN_SECONDS
                progress = nextProgress % singleIterationDuration

                while (usedBufferInSeconds >= PLAYER_NODE_MAX_BUFFER_LENGTH_IN_SECONDS) {
                    delay(BUFFER_LENGTH_IN_SECONDS.seconds / 2)
                }
            }
        } finally {
            audioPlayerNode.stop()
            audioEngine.stop()
            audioSession.setActive(false, error = null)
        }
    }

    actual fun getLatencyMs(): Int {
        // TODO: This is too much oversimplified estimation, find a better solution
        val ioBufferDuration = audioSession.IOBufferDuration()
        val latencyMs = (ioBufferDuration * 1000.0).toInt() - BUFFER_LENGTH_IN_SECONDS * 1000
        return latencyMs.coerceAtLeast(0)
    }

    private fun FloatArray.toAVAudioPCMBuffer(): AVAudioPCMBuffer {
        val buffer = AVAudioPCMBuffer(
            pCMFormat = audioFormat,
            frameCapacity = size.toUInt(),
        ).apply {
            frameLength = size.toUInt()
        }

        if (isNotEmpty()) {
            val channelPtr = buffer.floatChannelData!![0]!!.reinterpret<ByteVar>()
            usePinned { pinned ->
                memcpy(channelPtr, pinned.addressOf(0), (size * Float.SIZE_BYTES).toULong())
            }
        }

        return buffer
    }

    private fun convertDurationToFramesNumber(duration: Duration): Int {
        return convertDurationToFramesNumber(duration, SAMPLE_RATE, 1)
    }

    private companion object {
        const val SAMPLE_RATE = 44100
        const val BUFFER_LENGTH_IN_SECONDS = 2
        const val BUFFER_LENGTH_IN_FRAMES = SAMPLE_RATE * BUFFER_LENGTH_IN_SECONDS
        const val PLAYER_NODE_MAX_BUFFER_LENGTH_IN_SECONDS = BUFFER_LENGTH_IN_SECONDS * 2
    }
}
